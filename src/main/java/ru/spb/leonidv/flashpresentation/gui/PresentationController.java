package ru.spb.leonidv.flashpresentation.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;

import ru.spb.leonidv.flashpresentation.presentation.ImageInfo;
import ru.spb.leonidv.flashpresentation.presentation.Presentation;
import ru.spb.leonidv.flashpresentation.presentation.PresentationEvent;

/**
 * Класс инкапсулирует в себя всю работу с презентацией. При этом предоставляет доступ к элементам
 * представления презентацией и задания ее свойств.
 * 
 * В качестве атрибута можно задать JProgressBar, который будет отображать изменение
 * 
 * @author Leonid Vygovsky
 * 
 */
final class PresentationController {
    /**
     * Интерфейс описывает делегата, методы которого вызываются при начала длительных процессов
     * обработки презентации.
     * 
     * @author Leonid
     * 
     */
    static interface ProcessEvents {
        /**
         * Вызывается перед загрузкой презентации
         */
        void beforeLoad();

        /**
         * Вызывается после загрузки презентации
         */
        void afterLoad();
    };

    // Презентация, которая инкапсулируется в виде
    private Presentation presentation;

    // Модель данных для таблицы
    private PresentationTableModel tableModel;

    // Таблица, представляющая презентацию
    private JTable table;

    // Текстовое поле для представления сервера презентации
    private JTextField serverTextField;

    // Текстовое поле для представления заголовка презентации
    private JTextArea titleTextArea;

    // Панель, содержащая текстовые поля настройки презентации
    private JPanel infoPanel = null;

    // Компонент отображения прогресса загрузки изображений
    private JProgressBar progressBar = null;

    // Флаг, обозначает закончено выполнение загрузки презентации или нет
    private boolean busy;

    // Делегат, обрабатывающий различные действия при загрузке.
    private ProcessEvents eventsHandler = new DefaultProcessEvents();

    /**
     * Создает представление таблицы
     * 
     */
    public PresentationController() {
        presentation = new Presentation();
        tableModel = new PresentationTableModel(presentation);
        table = new JTable(tableModel);
        initTable();

        initButtons();
    }

    /**
	 * 
	 */
    private void initButtons() {
        serverTextField = new JTextField();
        titleTextArea = new JTextArea();

        DocumentListener serverEvents = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                System.out.println(serverTextField.getText());
                presentation.setServer(serverTextField.getText().trim());
            }

            public void removeUpdate(DocumentEvent e) {
                System.out.println(serverTextField.getText());
                presentation.setServer(serverTextField.getText().trim());
            };

        };

        DocumentListener titleEvents = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                presentation.setText(titleTextArea.getText().trim());
            }

            public void removeUpdate(DocumentEvent e) {
                presentation.setText(titleTextArea.getText().trim());
            }
        };

        serverTextField.getDocument().addDocumentListener(serverEvents);
        titleTextArea.getDocument().addDocumentListener(titleEvents);
    }

    /**
     * Вызывает метод сохранения презентации в XML-файл.
     * 
     * @return - истина, если презентация была успешно сохранена в XML-файл. Ложь в ином случае.
     */
    public boolean savePresentation() {
        return presentation.saveAsXML();
    }

    /**
     * Осуществляет загрузку презентации из папки, путь которой передается параметром.
     * 
     * @param pathToFolder
     *            - путь к папке с изображениями для презентации.
     * @throws IllegalArgumentException
     *             - бросается, если переданная строка не указывает на существующую директорию.
     * @throws IllegalStateException
     *             - бросается, если была начата длительная работа с презентацией и еще не
     *             закончена.
     * 
     */
    public void loadFromFolder(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException(
                    "Переданная строка не указавает на существующую папку");
        }
        ;

        if (busy) {
            throw new IllegalStateException("Ведется работа с презентацией");
        }

        PresentationEventHandler worker = new PresentationEventHandler(folder);
        presentation.setEventsHandler(worker);
        worker.execute();
    }

    /**
     * Возвращает таблицу представления презентации
     * 
     * @return - таблица представления презентации
     */
    public JTable getPresentationTable() {
        return table;
    }

    public JPanel getInfoPanel() {
        if (infoPanel == null) {
            infoPanel = createInfoPanel();
        }
        ;
        return infoPanel;
    }

    /**
     * Настраивает таблицу представления презентации
     * 
     * @return - новая таблица представления презентации.
     */
    private void initTable() {
        table.setVerifyInputWhenFocusTarget(false);
        setColumnWidth(PresentationTableModel.COLUMN_NUMBER, 40);
        setColumnWidth(PresentationTableModel.COLUMN_WIDTH, 100);
        setColumnWidth(PresentationTableModel.COLUMN_HEIGHT, 100);

        TableColumn column = table.getColumnModel().getColumn(
                PresentationTableModel.COLUMN_CONTROLS);
        column.setMaxWidth(124);
        column.setMinWidth(124);

        RowControlEditor controlEditor = new RowControlEditor(presentation);
        column.setCellEditor(controlEditor);
        column.setCellRenderer(controlEditor);

        column = table.getColumnModel().getColumn(PresentationTableModel.COLUMN_THUMBNAIL);
        column.setMaxWidth(ImageInfo.THUMBNAIL_WIDTH);
        column.setMinWidth(ImageInfo.THUMBNAIL_WIDTH);
        table.setRowHeight(ImageInfo.THUMBNAIL_HEIGHT);

        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Устанавливает максимальную ширину колонки.
     * 
     * @param columnIndex
     *            - номер колонки в таблице
     * @param maxWidth
     *            - максимальная ширина колонки
     */
    private void setColumnWidth(int columnIndex, int maxWidth) {
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(maxWidth);
    }

    private JPanel createInfoPanel() {
        JPanel result = new JPanel(new GridBagLayout());

        result.setPreferredSize(new Dimension(200, 150));

        serverTextField.setMaximumSize(new Dimension(100, 25));

        titleTextArea.setLineWrap(true);
        Border titleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Описание");
        JScrollPane scrollPane = new JScrollPane(titleTextArea);
        scrollPane.setPreferredSize(new Dimension(100, 150));
        scrollPane.setBorder(titleBorder);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = 3;
        constraints.ipady = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        result.add(new JLabel(" Сервер:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        result.add(serverTextField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;

        result.add(scrollPane, constraints);
        return result;
    }

    /**
     * Класс реализует обработку событий презентации. Наследуется от SwingWorker для обеспечения
     * нормальной работы приложения при загрузке презентации из каталога.
     * 
     * @author Leonid
     * 
     */
    private class PresentationEventHandler extends SwingWorker<Void, Integer> implements
            PresentationEvent {

        // Из которого следует загружать данные
        private File folder;

        public PresentationEventHandler(File folder) {
            super();
            this.folder = folder;
        }

        /**
         * Метод устанавливает максимальное значение полосы отображения прогресса загрузки
         * изображений.
         */
        public void startLoading(int count) {
            if (progressBar != null) {
                progressBar.setMaximum(count - 1);
                progressBar.setValue(0);
            }
        }

        public void move(int source, int dest) {
            // fireTableRowsUpdated принимает значения строго по увеличению
            if (source < dest) {
                tableModel.fireTableRowsUpdated(source, dest);
            } else {
                tableModel.fireTableRowsUpdated(dest, source);
            }
            table.getSelectionModel().setSelectionInterval(dest, dest);
        }

        public void remove(int index) {
            tableModel.fireTableRowsDeleted(index, index);
        }

        @Override
        protected Void doInBackground() throws Exception {
            eventsHandler.beforeLoad();
            busy = true;
            table.setEnabled(false);

            presentation.loadFromFolder(folder);

            table.setEnabled(true);
            busy = false;
            eventsHandler.afterLoad();
            return null;
        }

        @Override
        /**
         * Обновляем таблицу и увеличиваем значение в полосе прогресса.
         * 
         * Если выполнение уже завершено в главном потоке, этот метод еще может
         * вызываться. Тогда нужно обновить таблицу, а полосу прогресса уже
         * трогать не надо.
         */
        protected void process(List<Integer> list) {
            System.out.println(list);

            int currentElement = list.get(list.size() - 1);

            tableModel.fireTableRowsInserted(list.get(0), list.get(0));

            if (!isDone() && (progressBar != null)) {
                progressBar.setValue(currentElement + 1);
            }
        }

        public void imageLoaded(int index, ImageInfo imageInfo) {
            publish(index);
        }

    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setEventsHandler(ProcessEvents eventsHandler) {
        this.eventsHandler = eventsHandler;
    }
}

/**
 * Реализует шаблон нулевого объекта
 */
class DefaultProcessEvents implements PresentationController.ProcessEvents {
    public void afterLoad() {
    }

    public void beforeLoad() {
    }
}