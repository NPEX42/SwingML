package dev.npex42.swingml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SwingML {

    private static final Map<String, ComponentBuilder> builders = new HashMap<>();

    static {
        builders.put("Panel", (app, node) -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            app.addComponent(GetStringAttrib(node, "id"), panel, LayoutAttrib(node));
            app.pushParent(panel);
            AddNodes(app, node.getChildNodes());
            app.popParent();
        });

        builders.put("Window", (app, node) -> {
            app.setTitle(GetStringAttrib(node, "title"));
            AddNodes(app, node.getChildNodes());
        });

        builders.put("Label", (app, node) -> {
            JLabel label = new JLabel(node.getTextContent());
            app.addComponent(GetStringAttrib(node, "id"), label, LayoutAttrib(node));
        });

        builders.put("Button", (app, node) -> {
            JButton btn = new JButton(node.getTextContent());
            app.addComponent(GetStringAttrib(node, "id"), btn, LayoutAttrib(node));
        });

        builders.put("TextArea", (app, node) -> {
            JTextArea txt = new JTextArea(node.getTextContent().stripIndent());
            txt.setTabSize(4);
            int width = GetIntAttribOrDefault(node, "cols", 0);
            int height = GetIntAttribOrDefault(node, "rows", 0);
            txt.setRows(height);
            txt.setColumns(width);
            app.addComponent(GetStringAttrib(node, "id"), txt, LayoutAttrib(node));
        });
    }

    public static Application Create(String filename) throws RuntimeException {
        try {
            Application app = new Application();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filename));
            doc.getDocumentElement().normalize();
            AddNode(app, doc.getDocumentElement());
            return app;
        } catch (IOException | ParserConfigurationException | SAXException  ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void AddNode(Application app, Node node) {
        String type = node.getNodeName();
        System.out.println(type);
        if (builders.containsKey(type)) {
            builders.get(type).build(app, node);
        }
    }

    private static void AddNodes(Application app, NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            AddNode(app, nodes.item(i));
        }
    }

    private static String GetStringAttrib(Node node, String attr) {
        try {
            return node.getAttributes().getNamedItem(attr).getNodeValue();
        } catch (NullPointerException npex) {
            return "";
        }
    }

    private static String GetStringAttribOrDefault(Node node, String attr, String def) {
        try {
            return node.getAttributes().getNamedItem(attr).getNodeValue();
        } catch (NullPointerException npex) {
            return def;
        }
    }

    private static int GetIntAttrib(Node node, String attr) {
        return Integer.parseInt(GetStringAttrib(node, attr));
    }

    private static int GetIntAttribOrDefault(Node node, String attr, int d) {
        try {
            return Integer.parseInt(GetStringAttrib(node, attr));
        } catch (Exception ex) {
            return d;
        }
    }

    private static String LayoutAttrib(Node node) {
        return GetStringAttribOrDefault(node, "layout", BorderLayout.CENTER);
    }


    public interface ComponentBuilder {
        void build(Application app, Node node);
    }


}
