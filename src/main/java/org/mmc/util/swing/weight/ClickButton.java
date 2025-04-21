package org.mmc.util.swing.weight;

/**
 * 表示一个具有点击事件的按钮。
 */
public class ClickButton {

    // 按钮的标签，用于显示按钮上的文字
    private final String label;
    // 点击事件处理器，用于处理按钮的点击事件
    private final ClickHandler clickHandler;

    /**
     * 创建一个新的 Button 实例。
     *
     * @param label        按钮的标签
     * @param clickHandler 点击事件处理器
     */
    public ClickButton(String label, ClickHandler clickHandler) {
        this.label = label;
        this.clickHandler = clickHandler;
    }

    /**
     * 获取按钮的标签。
     *
     * @return 按钮的标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 触发点击事件。当调用此方法时，会调用点击事件处理器的 handleClick 方法
     */
    public void triggerClick() {
        clickHandler.handleClick(label);
    }

    /**
     * 定义按钮点击事件的处理接口。实现该接口的类需要实现 handleClick 方法
     */
    interface ClickHandler {

        /**
         * 处理按钮点击事件。
         *
         * @param btnName 发生点击事件的按钮标签
         */
        void handleClick(String btnName);
    }
}