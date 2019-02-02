import javax.swing.*;
import java.awt.*;

public class SettingWindow extends javax.swing.JDialog
{
    public SettingWindow(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);

        /*JFrame settingFrame = new JFrame("Cian");
        settingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingFrame.setPreferredSize(new Dimension(400, 120));
        settingFrame.pack();
        settingFrame.setLocationRelativeTo(null);
        settingFrame.setVisible(true);*/

        /*JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(400, 120));
        dialog.pack();
        dialog.setVisible(true);*/
    }

    public void execute()
    { // метод для "общения" с родительским окном
        this.setVisible(true); // прорисовать дочернее окно; в данном месте выполнение программы приостановится, ожидая окончания работы пользователя (в нашем случае - функции dispose())
        //return this.result; // вернуть в качестве результата условное значение нажатой кнопки
    }
}
