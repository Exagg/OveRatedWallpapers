package com.example.hrwallpapers;

public class booleanListeners {
    private boolean boo = false;
    private ChangeListener listener;

    public boolean isTrue() {
        return boo;
    }

    public void setValue(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
