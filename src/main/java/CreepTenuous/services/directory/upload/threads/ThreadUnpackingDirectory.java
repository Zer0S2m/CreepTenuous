package CreepTenuous.services.directory.upload.threads;

public class ThreadUnpackingDirectory extends Thread {
    public ThreadUnpackingDirectory(String name) {
        this.setName(name);
    }

    public void run() {
        try {
            Thread.sleep(1500);
            System.out.println(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
