package web.dev.webdev.messages;

public class ProgressMessage {
    private int progress;

    public ProgressMessage(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
