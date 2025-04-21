package org.mmc.util.javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 该类提供了一系列用于创建和显示JavaFX对话框的工具方法。
 * 包含了不同类型的对话框，如警告对话框、确认对话框、消息对话框、输入对话框等。
 */
public class JfxDialogUtils {

    /**
     * 显示一个警告对话框。
     *
     * @param alertType 警告对话框的类型，如ERROR、WARNING等
     * @param title     对话框的标题
     * @param message   对话框显示的消息内容
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.show();
    }

    /**
     * 显示一个确认对话框。
     *
     * @param title   对话框的标题
     * @param message 对话框显示的消息内容
     */
    public static void showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.show();
    }

    /**
     * 显示一个消息对话框，并在指定延迟时间后自动关闭。
     *
     * @param title           对话框的标题
     * @param message         对话框显示的消息内容
     * @param delayCloseTime  延迟关闭的时间（毫秒）
     */
    public static void showMsgDialog(String title, String message, int delayCloseTime) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (alert.isShowing()) {
                        alert.setResult(ButtonType.CLOSE); // 模拟点击关闭按钮
                    }
                });
            }
        }, delayCloseTime);
    }

    /**
     * 显示一个默认的消息对话框，标题为“提示”，消息为“操作成功！”，延迟3000毫秒关闭。
     */
    public static void showMsgDialog() {
        showMsgDialog("提示", "操作成功！", 3000);
    }

    /**
     * 显示一个消息对话框，标题为“提示”，自定义消息内容，延迟3000毫秒关闭。
     *
     * @param message 对话框显示的消息内容
     */
    public static void showMsgDialog(String message) {
        showMsgDialog("提示", message, 3000);
    }

    /**
     * 显示一个消息对话框，标题为“提示”，自定义消息内容和延迟关闭时间。
     *
     * @param message         对话框显示的消息内容
     * @param delayCloseTime  延迟关闭的时间（毫秒）
     */
    public static void showMsgDialog(String message, int delayCloseTime) {
        showMsgDialog("提示", message, delayCloseTime);
    }

    /**
     * 显示一个输入对话框，用户可以输入内容，并提供保存和关闭按钮。
     *
     * @param inputDialogCallBack 输入对话框的回调接口，用于处理保存和关闭事件
     */
    public static void showInputDialog(InputDialogCallBack inputDialogCallBack) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(200);
        dialog.setHeight(200);

        TextArea content = new TextArea();
        content.setWrapText(true);
        content.setPrefHeight(100);

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().addAll(JfxCustomWight.getButton("保存", btn -> {
            inputDialogCallBack.onSave(content.getText());
            dialog.close();
        }), JfxCustomWight.getButton("关闭", btn -> {
            inputDialogCallBack.onClose(content.getText());
            dialog.close();
        }));

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(content, flowPane);

        Scene dialogScene = new Scene(vBox, 200, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * 显示一个包含标题输入框和内容输入框的弹框，用户可以输入标题和内容，并提供保存、关闭和取消按钮。
     *
     * @param inputDialogCallBack 输入对话框的回调接口，用于处理关闭事件
     */
    public static void showTitleContentInputDialog(InputTitleContentDialogCallBack inputDialogCallBack) {
        Stage dialog = new Stage();
        dialog.setTitle("提示");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(200);
        dialog.setHeight(200);

        TextField title = new TextField();
        title.setPrefHeight(20);
        TextArea content = new TextArea();
        content.setPrefHeight(80);
        content.setWrapText(true);

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.getChildren().addAll(JfxCustomWight.getButton("保存", btn -> {
            inputDialogCallBack.onClose(title.getText(), content.getText());
            dialog.close();
        }), JfxCustomWight.getButton("关闭", btn -> {
            dialog.close();
        }), JfxCustomWight.getButton("取消", btn -> {
            dialog.close();
        }));

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(title, content, flowPane);

        Scene dialogScene = new Scene(vBox, 200, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * 显示一个包含二维码图片和消息的对话框。
     *
     * @param msg 要生成二维码的消息内容
     */
    public static void showQrImgDialog(String msg) {
        DialogPane grid = new DialogPane();
        grid.setPadding(new Insets(5));

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Image qrCodeImage = generateQRCodeImage(msg);
        ImageView customImage = new ImageView(qrCodeImage);

        Label label = new Label(msg);

        vBox.getChildren().addAll(customImage, label);
        grid.setContent(vBox);

        Dialog<String> dlg = new Dialog<>();
        Window window = dlg.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event1 -> window.hide());
        dlg.setDialogPane(grid);
        dlg.show();
    }

    /**
     * 生成二维码图片的方法，目前尚未实现。
     *
     * @param text 要生成二维码的文本内容
     * @return 生成的二维码图片
     */
    private static Image generateQRCodeImage(String text) {
        // TODO: 2023/10/13
        //使用qrgen库生成二维码
//        byte[] qrCodeBytes = QRCode.from(text).to(ImageType.PNG).stream().toByteArray();
//        return new Image(new ByteArrayInputStream(qrCodeBytes));
        return null;
    }

    /**
     * 自定义控件的回调接口，用于处理输入对话框的保存和关闭事件。
     */
    public interface InputDialogCallBack {

        /**
         * 当用户点击保存按钮时调用。
         *
         * @param content 用户输入的内容
         */
        void onSave(String content);

        /**
         * 当用户点击关闭按钮时调用，默认实现为空。
         *
         * @param content 用户输入的内容
         */
        default void onClose(String content) {

        }
    }

    /**
     * 自定义控件的回调接口，用于处理包含标题和内容输入框的对话框的关闭事件。
     */
    public interface InputTitleContentDialogCallBack {

        /**
         * 当用户点击保存或关闭按钮时调用。
         *
         * @param title   用户输入的标题
         * @param content 用户输入的内容
         */
        void onClose(String title, String content);
    }

}