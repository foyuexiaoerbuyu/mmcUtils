package org.mmc.util.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 该类提供了一系列自定义的JavaFX控件和相关工具方法。
 * 包含了文本框、按钮、标签、文本区域等控件的创建和处理逻辑。
 */
public class JfxCustomWight {

    /**
     * 创建一个指定宽度和高度的文本框，并添加拖拽和回车事件处理。
     * 默认不启用拖拽功能。
     *
     * @param w                   文本框的宽度
     * @param h                   文本框的高度
     * @param iTextFieldCallBack  文本框的回调接口，用于处理拖拽和回车事件
     * @return 创建好的文本框
     */
    public static TextField getTextField(int w, int h, ITextFieldCallBack iTextFieldCallBack) {
        return getTextField(w, h, false, iTextFieldCallBack);
    }

    /**
     * 创建一个指定宽度和高度的文本框，并可以选择是否启用拖拽功能，同时添加拖拽和回车事件处理。
     *
     * @param w                   文本框的宽度
     * @param h                   文本框的高度
     * @param isDrag              是否启用拖拽功能
     * @param iTextFieldCallBack  文本框的回调接口，用于处理拖拽和回车事件
     * @return 创建好的文本框
     */
    public static TextField getTextField(int w, int h, boolean isDrag, ITextFieldCallBack iTextFieldCallBack) {
        TextField textField = new TextField();
        textField.setPrefSize(w, h);
        if (isDrag) {
            //<editor-fold desc="拖拽功能">
            textField.setOnDragOver(event -> {
                if (event.getGestureSource() != textField && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
            });

            textField.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;
                if (dragboard.hasFiles()) {
                    List<File> files = dragboard.getFiles();
                    // 处理拖放的文件
                    StringBuilder filePaths = new StringBuilder();
                    for (File file : files) {
                        String filePath = file.getAbsolutePath();
                        filePaths.append(filePath).append("\n");
                        // 在这里使用文件路径进行操作
                        System.out.println(filePath);
                    }
                    success = true;
                    textField.setText(filePaths.toString());
                    iTextFieldCallBack.onDragFiles(files);
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
        //</editor-fold>
        if (iTextFieldCallBack != null) {
            // 添加回车按键监听
            textField.setOnAction(actionEvent -> {

                String trim = textField.getText().trim();
                iTextFieldCallBack.onClickEnter(textField, textField.getText(), startsWithWindowsDrive(trim));
            });

            // 添加ctrl+回车按键监听
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isControlDown()) {
                        // 插入换行符
                        textField.insertText(textField.getCaretPosition(), System.getProperty("line.separator"));
                        String trim = textField.getText().trim();
                        iTextFieldCallBack.onClickCtrlEnter(textField, textField.getText(), startsWithWindowsDrive(trim));
                    }
                }
            });
        }
        return textField;
    }

    /**
     * 创建一个带有点击和长按事件处理的按钮。
     *
     * @param text            按钮上显示的文本
     * @param iClickCallBack  按钮的回调接口，用于处理点击和长按事件
     * @return 创建好的按钮
     */
    public static Button getButton(String text, IClickCallBack iClickCallBack) {
        AtomicBoolean isLong = new AtomicBoolean(false);
        Button btn = new Button(text);
        if (iClickCallBack != null) {
            btn.setOnAction(event -> {//点击事件
                if (isLong.get()) {
                    isLong.set(false);
                    return;
                }
                iClickCallBack.onClick(btn);
            });

            final Timeline longPress = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                //长按事件
                iClickCallBack.onLongClick();
                isLong.set(true);
            }));
            longPress.setCycleCount(1);

            btn.setOnMousePressed(e -> longPress.playFromStart());
            btn.setOnMouseReleased(e -> longPress.stop());
        }

        return btn;
    }

    /**
     * 创建一个带有鼠标点击事件处理的标签。
     *
     * @param txt      标签上显示的文本
     * @param iClick   标签的回调接口，用于处理点击事件
     * @return 创建好的标签
     */
    public static Label getLabel(String txt, IClick iClick) {
        Label lab = new Label(txt);
        // 为Label对象添加鼠标点击事件处理程序
        lab.setOnMouseClicked((MouseEvent event) -> {
            if (iClick != null) {
                iClick.click(lab, lab.getText());
                System.out.println("Label被点击了");
            }
        });

        return lab;
    }

    /**
     * 创建一个带有回车监听的文本区域。
     *
     * @param iTextAreaCallBack 文本区域的回调接口，用于处理回车事件
     * @return 创建好的文本区域
     */
    public static TextArea getTextArea(ITextAreaCallBack iTextAreaCallBack) {
        return getTextArea(iTextAreaCallBack, null);
    }

    /**
     * 创建一个带有回车监听和文本选择监听的文本区域。
     *
     * @param iTextAreaCallBack  文本区域的回调接口，用于处理回车事件
     * @param changeListener     文本选择的更改监听器
     * @return 创建好的文本区域
     */
    public static TextArea getTextArea(ITextAreaCallBack iTextAreaCallBack, ChangeListener<String> changeListener) {
        TextArea textArea = new TextArea();
        // 监听选择文本的更改
        if (changeListener != null) {
            textArea.selectedTextProperty().addListener(changeListener);
        }

        textArea.setOnKeyPressed(event -> {
            if (iTextAreaCallBack == null) return;
            if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                // 插入换行符
                textArea.insertText(textArea.getCaretPosition(), System.getProperty("line.separator"));
                iTextAreaCallBack.ctrlEnter(textArea);
                System.out.println("Ctrl+Enter key pressed");
            } else if (event.getCode() == KeyCode.ENTER) {
                iTextAreaCallBack.enter(textArea);
                System.out.println("Enter key pressed");
            }
        });
        return textArea;
    }

    /**
     * 打开一个文件选择对话框，允许用户选择多个文件。
     *
     * @param primaryStage 主舞台
     */
    public static void selFiles(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有文件", "*.*"));

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    public static MenuItem getMenuItem(String btnName, EventHandler<ActionEvent> eventHandler) {
        // 创建菜单项
        MenuItem clearItem1 = new MenuItem(btnName);
        clearItem1.setOnAction(eventHandler);
        return clearItem1;
    }

    /**
     * 是否为盘符开头
     */
    public static boolean startsWithWindowsDrive(String str) {
        if (str == null) return false;
        // 使用正则表达式判断字符串是否以 Windows 盘符开头
        return str.trim().matches("^[A-Za-z]:\\\\.*");
    }

    /**
     * 自定义控件
     */
    public interface IClickCallBack {

        void onClick(Button btn);

        default void onLongClick() {

        }
    }

    /**
     * 自定义控件
     */
    public interface ITextFieldCallBack {
        /**
         *
         * @param textField 文本框
         * @param text 文本
         * @param isFile 是否为拖拽的文件/也有可能是文件夹
         */
        void onClickEnter(TextField textField, String text, boolean isFile);

        /**
         * @param isFile 是否为拖拽的文件/也有可能是文件夹
         */
        default void onClickCtrlEnter(TextField textField, String text, boolean isFile) {

        }

        /**
         * @param files 拖拽文件
         */
        default void onDragFiles(List<File> files) {

        }
    }


    /**
     * IClick接口定义了点击事件的处理方法
     * 用于规范点击事件的响应行为，包括普通点击和长按点击
     */
    public interface IClick {
        /**
         * 处理普通点击事件的方法
         *
         * @param obj  对象，代表点击事件发生的目标对象
         * @param text 文字，代表与点击事件相关联的文本信息
         */
        void click(Object obj, String text);

        /**
         * 处理长按点击事件的默认方法
         *
         * @param obj  对象，代表长按点击事件发生的目标对象
         * 此方法默认实现为空，子类可以根据需要提供具体实现
         */
        default void longClick(Object obj) {
            // 此处可以根据需要添加长按点击的处理逻辑
        }
    }

    /**
     * ITextAreaCallBack接口用于定义文本区域的回调方法
     * 该接口主要处理两种键盘事件：Ctrl + Enter和Enter
     */
    public interface ITextAreaCallBack {

        /**
         * 当用户按下Ctrl + Enter键时调用的方法
         * 此方法可以由实现类覆盖以提供具体的操作逻辑
         *
         * @param textArea 发生键盘事件的文本区域对象
         */
        default void ctrlEnter(TextArea textArea) {
            // 此处可根据需要添加对Ctrl + Enter事件的处理逻辑
        }

        /**
         * 当用户按下Enter键时调用的方法
         * 此方法可以由实现类覆盖以提供具体的操作逻辑
         *
         * @param textArea 发生键盘事件的文本区域对象
         */
        default void enter(TextArea textArea) {
            // 此处可根据需要添加对Enter事件的处理逻辑
        }
    }


    /**
     * 创建一个具有选择监听功能的 ComboBox
     *
     * @param handler 选择事件处理器
     * @param items   下拉框中的选项
     * @param <T>     下拉框中选项的类型
     * @return 创建好的 ComboBox
     */
    @SafeVarargs
    public static <T> ComboBox<T> createComboBox(ComboBoxSelectionHandler<T> handler, T... items) {
        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(items));
// 设置默认选择
        if (!comboBox.getItems().isEmpty()) {
            comboBox.getSelectionModel().selectFirst();
        }
        // 自定义行为
        comboBox.setCellFactory(lv -> {
            ListCell<T> cell = new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    comboBox.getSelectionModel().select(index);
                    handler.handleSelection(index, comboBox);
                    event.consume();
                }
            });

            return cell;
        });

        return comboBox;
    }

    /**
     * 泛型的 ComboBox 选择事件处理器接口
     *
     * @param <T> 下拉框中选项的类型
     */
    @FunctionalInterface
    public interface ComboBoxSelectionHandler<T> {
        void handleSelection(int index, ComboBox<T> comboBox);
    }

}
