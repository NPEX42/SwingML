package dev.npex42.swingml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Application extends JFrame {
    private Map<String, JComponent> components = new HashMap<>();
    private Stack<Container> parent = new Stack<>();

    private final Map<String, ActionListenerHook> actionListeners = new HashMap<>();

    public Application() {
        parent.push(this.getContentPane());
    }

    public void start() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                        setSize(1080, 720);
                        setVisible(true);
                        setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
            );
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addComponent(String id, JComponent component, String layout) {
        components.put(id, component);
        actionListeners.put(id, new ActionListenerHook(this));
        parent.peek().add(component, layout);

        System.out.printf("%s -> %s%n", id, component);
    }

    protected void addComponent(String id, JComponent component) {
        components.put(id, component);
        actionListeners.put(id, new ActionListenerHook(this));
        parent.peek().add(component);
        System.out.printf("%s -> %s%n", id, component);
    }

    protected void pushParent(Container p) {
        parent.push(p);
    }

    protected void popParent() {
        if (parent.size() > 1) {
            parent.pop();
        }
    }

    public <T> T getComponent(String id) {
        return (T) components.get(id);
    }

    public void addActionListener(String id, ActionListener al) {
        AbstractButton el = getComponent(id);
        el.addActionListener(al);
    }

    public static class ActionListenerHook implements ActionListener {

        private ActionListenerHook(Application app) {
            this.app = app;
        }

        private Application app;
        @Override
        public final void actionPerformed(ActionEvent e) {
            actionPerformed(app, e);
        }

        void actionPerformed(Application app, ActionEvent e) {}
    }
}
