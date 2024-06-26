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
    requires sym.system.core;
    exports com.yujigu.echolink;
    exports com.yujigu.echolink.model;
    exports com.yujigu.echolink.listener;

    opens com.yujigu.echolink.listener to javafx.fxml, org.slf4j;
    opens com.yujigu.echolink to javafx.fxml, org.slf4j;
    opens com.yujigu.echolink.model to javafx.fxml,  org.slf4j;
}
