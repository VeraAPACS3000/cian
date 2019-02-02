import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import javax.script.ScriptException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.awt.event.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


public class MainWindow extends JFrame
{
    String strPath = "";
    String strUrl = "";

    private JButton button1 = null;
    private JTextField textFieldUrl = null;
    private JFileChooser fileChooser = null;
    private JFrame frame = null;
    private JMenuBar menuBar = null;
    private JMenu menuItemSetting = null;
    private JLabel label = null;
    private JPanel panel = null;


    //=================для модального окна=====================================
    private JDialog settingWindow = null;
    private JPanel panelModalWindow1 = null;
    private JPanel panelModalWindow2 = null;
    private JSplitPane splitPane = null;
    private Component labelModal1 = null;
    private Component labelModal2 = null;
    private JTextField textFieldPath = null;
    private JButton btnSelectPath = null;

    File file = null;
    String strKeyXmlConf = "";
    XMLConfiguration config = null;

    /*private static class SingletonHolder
    {
        private static MainWindow instance = new MainWindow();

        private final static MainWindow getInstance()
        {
            return SingletonHolder.instance;
        }
    }*/

    public MainWindow()
    {
        btnSelectPath = new JButton("Открыть");
        CreateGUI();
    }

    public void CreateGUI()
    {
        try {
            config = new XMLConfiguration("settings.xml");
            for(Iterator iter = config.getKeys(); iter.hasNext(); )
            {
                String item = (String) iter.next();
                if(item.equalsIgnoreCase("resource.path"))
                {
                    //Взяли из config файла.
                    strKeyXmlConf = config.getProperty(item).toString();
                    System.out.println("strKeyXmlConf: " + strKeyXmlConf);
                }
                System.out.println("  " + item + " = " + config.getProperty(item));
            }

            //Если нет значения в config файле. Берем домашнюю директорию.
            if(strKeyXmlConf.equalsIgnoreCase(""))
            {
                file = new File( System.getProperty("user.home"));
                strKeyXmlConf = file.getPath();
                System.out.println("Взяли домашнюю директорию. strKeyXmlConf: " + strKeyXmlConf);
            }
            else
            {
                //Есть путь в файле.
                file = new File(strKeyXmlConf);
            }
            /*strKeyXmlConf = config.getProperties("resource.path").toString();
            if(!config.containsKey("resource") || strKeyXmlConf.equalsIgnoreCase(""))
            {
                config.addProperty("resource", file.getPath());
            }
            System.out.println("strKeyXmlConf: " + strKeyXmlConf);*/
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        frame = new JFrame("Cian");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font font = new Font("Verdana", Font.PLAIN, 11);

        /*===== MENU BAR =====*/
        menuBar = new JMenuBar();
        menuItemSetting = new JMenu("Настройки");
        menuItemSetting.setFont(font);
        menuBar.add(menuItemSetting);

        /*===== PANEL  =====*/
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        /*===== BUTTON =====*/
        button1 = new JButton("Запустить");
        //button1.setAlignmentX(BOTTOM_ALIGNMENT);
        panel.add(button1, BorderLayout.PAGE_END);

        /*===== TEXT FIELD  =====*/
        textFieldUrl = new JTextField();
        textFieldUrl.setText("123");
        //textField1.setColumns(23);
        //textField1.setPreferredSize(new Dimension(80,20));
        panel.add(textFieldUrl, BorderLayout.CENTER);

        /*===== надпись, что сделать  =====*/
        label = new JLabel();
        label.setFont(font);
        label.setText("Введите url запроса, сделанного в «Cian»:");
        panel.add(label, BorderLayout.PAGE_START);

        getContentPane().add(panel);
        setPreferredSize(new Dimension(320, 100));

        frame.setContentPane(getContentPane());
        frame.setJMenuBar(menuBar);
        frame.setPreferredSize(new Dimension(400, 120));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //========================Listener==================================================================
        //button RUN!
        button1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Parser parser = new Parser();
                    String result = null;
                    try {
                        result = parser.mainParser(strKeyXmlConf, //куда сохранять.
                                                    textFieldUrl.getText());//ссылка на скачивание
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                    if(result.equalsIgnoreCase("0"))
                    {
                        JOptionPane.showMessageDialog(frame, "CAPTCHA!!!","Error", JOptionPane.ERROR_MESSAGE);
                    }
                }catch (NoSuchMethodException e1)
                {
                    e1.printStackTrace();
                }
            }
        });

        menuItemSetting.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                settingWindow = new JDialog(frame, true);
                settingWindow.setTitle("Настройки");
                settingWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                settingWindow.setPreferredSize(new Dimension(600, 350));

                Container contentPane = settingWindow.getContentPane();

                SpringLayout layout = new SpringLayout();
                contentPane.setLayout(layout);

                labelModal1 = new JLabel("Выбрать место для сохранения результатов");
                labelModal2 = new JLabel("Личные данные");
                //===== для выбора куда сохранять =====
                fileChooser = new JFileChooser();
                textFieldPath = new JTextField();
                textFieldPath.setPreferredSize(new Dimension(300,25));
                //показываем,что было выбрано перед этим.
                textFieldPath.setText(strKeyXmlConf);


                btnSelectPath.setPreferredSize(new Dimension(100,25));


                contentPane.add(labelModal1);
                contentPane.add(labelModal2);
                contentPane.add(textFieldPath);
                contentPane.add(btnSelectPath);
                //1ый label.
                layout.putConstraint(SpringLayout.WEST , labelModal1, 10,
                        SpringLayout.WEST , contentPane);
                layout.putConstraint(SpringLayout.NORTH, labelModal1, 13,
                        SpringLayout.NORTH, contentPane);
                //2ой label.
                layout.putConstraint(SpringLayout.WEST , labelModal2, 10,
                        SpringLayout.WEST , contentPane      );
                layout.putConstraint(SpringLayout.NORTH, labelModal2, 40,
                        SpringLayout.SOUTH, labelModal1);


                //====TextEdit path.
                layout.putConstraint(SpringLayout.WEST , textFieldPath, 10,
                        SpringLayout.WEST , contentPane);
                layout.putConstraint(SpringLayout.NORTH, textFieldPath, 5,
                        SpringLayout.SOUTH, labelModal1);
                //====Кнопка.
                layout.putConstraint(SpringLayout.WEST , btnSelectPath, 10,
                        SpringLayout.EAST , textFieldPath);
                layout.putConstraint(SpringLayout.NORTH, btnSelectPath, 5,
                        SpringLayout.SOUTH, labelModal1);

                settingWindow.pack();
                settingWindow.setLocationRelativeTo(null);
                settingWindow.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e)
            {

            }
        });

        //Выбор файла.
        btnSelectPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор директории");
                fileChooser.setCurrentDirectory(file);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int ret = fileChooser.showDialog(frame, "Сохранить");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    strKeyXmlConf = file.getPath();
                    //textFieldPath.setText(file.getPath());
                    textFieldPath.setText(strKeyXmlConf);
                    config.addProperty("resource.path", strKeyXmlConf);
                    config.clearProperty("resource.path");
                    config.addProperty("resource.path", strKeyXmlConf);
                    try {
                        config.save("settings.xml");
                    } catch (ConfigurationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });    }

    public static void main(String[] args)
    {        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                MainWindow mainWindow = new MainWindow();
            }
        });
    }
}