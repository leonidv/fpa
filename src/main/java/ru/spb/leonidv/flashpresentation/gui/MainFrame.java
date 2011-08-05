/*
 * MainFrame.java
 *
 * Created on 2 Ноябрь 2006 г., 21:51
 */

package ru.spb.leonidv.flashpresentation.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 * @author Leonid
 */
public class MainFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 8889959318103930458L;

    // Представление презентации
    PresentationController presentationController = new PresentationController();

    // Поле ввода пути к файлам презентации
    private JTextField textPath;

    // Полоска прогресса обработки задания
    private JProgressBar progressBar = new JProgressBar();

    // Панель управления презентацией (открытие/сохранение)
    private JPanel panelPresentationControls;

    // Обработчик начала и конца загрузки презентации
    private PresentationController.ProcessEvents presentationProcessEvents =

    new PresentationController.ProcessEvents() {

        public void afterLoad() {
            progressBar.setVisible(false);
            setEnablePanel(panelPresentationControls, true);
        }

        public void beforeLoad() {
            setEnablePanel(panelPresentationControls, false);
            progressBar.setVisible(true);
            // Мелкий трюк
            progressBar.setEnabled(true);
        }

    };

    /**
     * Возвращает текущий каталог, из которого берутся картинки для презентации. Если такой каталог
     * еще не выбран или он не существует, то берется текущий каталог, из которого выбранна
     * программа.
     */
    private File getCurrentPresentationDir() {

        File currentDir = new File(textPath.getText().trim());
        if (!(currentDir.exists() && currentDir.isDirectory())) {
            currentDir = new File(".");
        }
        return currentDir;
    }

    /**
     * Устанавливает новый каталог с презентацией.
     */
    private void setCurrentPresentationDir(File file) {
        textPath.setText(file.getPath());
    }

    private void buttonLoadPresentationAction(java.awt.event.ActionEvent evt) {
        presentationController.loadFromFolder(getCurrentPresentationDir());
    }

    private void buttonLoadAction(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser(getCurrentPresentationDir());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setCurrentPresentationDir(fileChooser.getSelectedFile());
        }
    }

    private void buttonSaveAction(ActionEvent evt) {
        presentationController.savePresentation();
    }

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.setSize(900, 600);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);
        try {
            setIconImage(ImageIO.read(getClass().getResource("icon.png")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        showWindow();
    }

    /**
     * Создает и показывает фрейм.
     */
    public static void showWindow() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FlashPresentation Ananas");
        setName("frameMain");

        /*
         * Размещаем компоненты на форме
         */
        getContentPane().add(new JScrollPane(presentationController.getPresentationTable()),
                BorderLayout.CENTER);
        getContentPane().add(createSelectPathPanel(), BorderLayout.NORTH);
        getContentPane().add(createDataManipulationPanel(), java.awt.BorderLayout.EAST);
        getContentPane().add(createPresentationControlsPanel(), java.awt.BorderLayout.SOUTH);

        /*
         * Настраиваем контроллер презентации
         */
        presentationController.setProgressBar(progressBar);
        presentationController.setEventsHandler(presentationProcessEvents);

        pack();
    }

    /**
     * @return
     */
    private JPanel createSelectPathPanel() {
        JPanel panelPath = new JPanel(new BorderLayout());
        panelPath.setBorder(BorderFactory.createTitledBorder("Каталог с изображениями"));

        textPath = new javax.swing.JTextField();

        JButton buttonSelectDir = new JButton("Выбрать каталог");
        buttonSelectDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadAction(evt);
            }
        });

        panelPath.add(textPath, BorderLayout.CENTER);
        panelPath.add(buttonSelectDir, BorderLayout.EAST);
        return panelPath;
    }

    /**
     * Создает панель управления изображениями в коллекции.
     * 
     * @return
     */
    private javax.swing.JPanel createDataManipulationPanel() {
        JPanel result = new JPanel(new BorderLayout());
        BorderLayout resultLayout = new BorderLayout();
        result.setLayout(resultLayout);
        result.add(presentationController.getInfoPanel(), BorderLayout.SOUTH);
        return result;
    }

    /**
     * Создает и настраивает панель управления презентацией.
     * 
     * @return - панель управления презентацией.
     */
    private JPanel createPresentationControlsPanel() {
        panelPresentationControls = new JPanel(new GridBagLayout());
        JButton buttonLoadPresentation = new JButton("Загрузить изображения из каталога");
        JButton buttonSavePresentation = new JButton("Сохранить презентацию в XML-файл");

        buttonLoadPresentation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonLoadPresentationAction(e);
            }
        });

        buttonSavePresentation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonSaveAction(e);
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        panelPresentationControls.add(buttonLoadPresentation, constraints);

        constraints.gridx = 1;
        panelPresentationControls.add(buttonSavePresentation, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panelPresentationControls.add(progressBar, constraints);

        progressBar.setVisible(false);
        return panelPresentationControls;
    }

    // Устанавливает свойста enabled для панели и всех ее компонент.
    private void setEnablePanel(JPanel panel, boolean isEnabled) {
        panel.getComponentCount();
        for (Component component : panel.getComponents()) {
            component.setEnabled(isEnabled);
        }
    };

}
