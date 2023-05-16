
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.google.zxing.*;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class Main extends JFrame implements Runnable/*, WebcamPanel.Painter*/ {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static volatile boolean stop;
    private static ResultPoint[] points = new ResultPoint[0];
    Frame frame = new Frame();
    CanvasFrame canvas = new CanvasFrame("Web Cam");
    // Webcam
    // Webcam webcam;
    /*static {
        Webcam.setDriver(new V4l4jDriver()); // this is important
    }*/
    //private WebcamPanel.Painter painter = null;

    FrameGrabber grabber = new OpenCVFrameGrabber(0);
    SortingServiceHttpClient client;

    RaspberryIO raspberryIO = new RaspberryIO();
    Main(String apiAddress) throws IOException, URISyntaxException, InterruptedException {
        super();
        final Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client = new SortingServiceHttpClient(apiAddress);
        //client.getSortingLineParameter(-1);

        grabber.start();
        Thread thread = new Thread(this);
        thread.start();
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Webcam.getWebcams().stream().map(Webcam::getName).forEach(System.out::println);
        new Main(args[0]);
    }

    @Override
    public void run() {
        int i = 0;


        int lastDelivery = -1;
        while (!stop) {
            try (var converter = new Java2DFrameConverter()) {
                MultiFormatReader reader = new MultiFormatReader();
                frame = grabber.grabFrame();

                canvas.showImage(frame);

                var image = converter.convert(frame);

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = reader.decode(bitmap);

                if (result.getResultPoints().length > 0) {
                    points = result.getResultPoints();
                } else {
                    points = new ResultPoint[0];
                }

                //System.out.println(result.getText());
                var deliveryId = Integer.parseInt(result.getText());
                if (lastDelivery != deliveryId) {
                    System.out.println("ID доставки: " + deliveryId);

                    try {
                        var line = client.getSortingLineParameter(deliveryId);
                        System.out.println("Сортировочная линия " + line);
                        raspberryIO.enableLight(line);
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                    lastDelivery = deliveryId;
                }
            } catch (FrameGrabber.Exception | NotFoundException e) {
                //throw new RuntimeException(e);
            }
        }
    }

    static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }
}
