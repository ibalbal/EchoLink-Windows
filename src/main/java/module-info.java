module com.yujigu.echolink {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.web;
    requires sym.system.jni;
    requires lombok;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop;
    requires javax.websocket.api;
    requires com.alibaba.fastjson2;
    requires org.slf4j;
    opens com.yujigu.echolink.listener to org.glassfish.tyrus.core;
    opens com.yujigu.echolink to javafx.fxml, org.glassfish.tyrus.core, org.slf4j;
    exports com.yujigu.echolink;
    opens com.yujigu.echolink.service to org.glassfish.tyrus.core;
}
