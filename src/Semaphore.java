import java.util.List;
import java.util.concurrent.Semaphore;

//public class Semaphore {
//    private int value;
//
//
//    public void do() {
//        Object.wait();
//    }
//    // => th1 | th2 | th3 | th4
//    public void release() {
//        notify();
//    }
//}

enum DeviceType {
    Mobile, PC, Tablet
}

class Device implements Runnable {
    private String name;
    private DeviceType type;
    private Router router;

    public Device(String name, DeviceType type, Router router) {
        this.router = router;
        this.name = name;
        this.type = type;
    }

    @Override
    public void run() {
        router.connect();

        // Wait random time.
        // Do some activity.
        // Wait random time.

        router.release();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public void logout() {

    }
}

class Router {
    List<Device> devices;
    Semaphore semaphore = new Semaphore(5);

    public Router() {
    }

    public void connect() {
        try {
            semaphore.acquire();

        }
        catch (Exception e) {

        }
    }

    public void release() {
        try {
            semaphore.release();
        }
        catch (Exception e) {

        }
    }
}

class Network {
    static void main() {
        Router router = new Router();

        Device d1 = new Device("d1", DeviceType.PC, router);

        d1.run();

        List<Device> devices;

    }
}
