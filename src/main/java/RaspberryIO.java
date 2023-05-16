import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.platform.Platforms;

public class RaspberryIO {
    private static final int PIN_LED_1 = 23;
    private static final int PIN_LED_2 = 24;
    private static final int PIN_LED_3 = 25;
    private static final int PIN_LED_DEF = 16;
    private DigitalOutput led1;
    private DigitalOutput led2;
    private DigitalOutput led3;
    private DigitalOutput ledDef;
    private Context pi4j;

    public RaspberryIO() {
        pi4j = Pi4J.newAutoContext();
        Platforms platforms = pi4j.platforms();
        configureLed1(pi4j);

        configureLed2(pi4j);
        configureLed3(pi4j);
        configureLedDef(pi4j);
        ledDef.high();
    }

    public void enableLight(int num) {
        disableAllLights();
        switch (num) {
            case 0:
                led1.high();
                break;
            case 1:
                led2.high();
                break;
            case 2:
                led3.high();
                break;
            default:
                ledDef.high();
                break;
        }
    }

    private void disableAllLights() {
        led1.low();
        led2.low();
        led3.low();
        ledDef.low();
    }

    public void shutdown() {
        pi4j.shutdown();
    }

    private void configureLed1(Context pi4j) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led_1")
                .name("LED Flasher_1")
                .address(PIN_LED_1)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        led1 = pi4j.create(ledConfig);
    }

    private void configureLed2(Context pi4j) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led_2")
                .name("LED Flasher_2")
                .address(PIN_LED_2)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        led2 = pi4j.create(ledConfig);

    }

    private void configureLed3(Context pi4j) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led_3")
                .name("LED Flasher_3")
                .address(PIN_LED_3)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        led3 = pi4j.create(ledConfig);
    }

    private void configureLedDef(Context pi4j) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led_def")
                .name("LED Flasher_def")
                .address(PIN_LED_DEF)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        ledDef = pi4j.create(ledConfig);
    }
}
