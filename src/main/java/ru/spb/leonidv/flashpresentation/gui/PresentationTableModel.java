/*
 * PresentationTableModel.java
 *
 * Created on 8 Ноябрь 2006 г., 22:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ru.spb.leonidv.flashpresentation.gui;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import ru.spb.leonidv.flashpresentation.presentation.ImageInfo;
import ru.spb.leonidv.flashpresentation.presentation.Presentation;

/**
 * Модель данных для представления в таблице.
 * 
 * @author Leonid Vygovsky
 */
public class PresentationTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 8640948889135828980L;

    // Номер колонки с номером изображения
    public final static int COLUMN_NUMBER = 0;

    // Номер колонки с именем изображения
    public final static int COLUMN_NAME = 1;

    // Номер колонки с шириной изображения
    public final static int COLUMN_WIDTH = 2;

    // Номер колонки с высотой изображения
    public final static int COLUMN_HEIGHT = 3;

    // Номер колонки с миниатюрой изображения
    public final static int COLUMN_THUMBNAIL = 4;

    // Номер колонки элементами управления
    public final static int COLUMN_CONTROLS = 5;

    // Массив названий колонок
    private final static String[] COLUMN_TITLES;
    static {
        COLUMN_TITLES = new String[6];
        COLUMN_TITLES[COLUMN_NUMBER] = "№";
        COLUMN_TITLES[COLUMN_NAME] = "Изображение";
        COLUMN_TITLES[COLUMN_WIDTH] = "Ширина";
        COLUMN_TITLES[COLUMN_HEIGHT] = "Высота";
        COLUMN_TITLES[COLUMN_THUMBNAIL] = "Миниатюра";
        COLUMN_TITLES[COLUMN_CONTROLS] = "Управление";
    }

    // Данные таблицы. Ссылка на внешнюю структуру.
    Presentation presentation;

    /**
     * @exception NullPointerException
     *                - бросает исключение, если <code>presentation == null</code>.
     */
    public PresentationTableModel(Presentation presentation) {
        if (presentation == null) {
            throw new NullPointerException();
        }

        this.presentation = presentation;
    }

    public int getRowCount() {
        return presentation.size();
    }

    public int getColumnCount() {
        return 6;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ImageInfo imageInfo = presentation.getImageInfo(rowIndex);

        switch (columnIndex) {
        case COLUMN_NUMBER:
            return rowIndex + 1;
        case COLUMN_NAME:
            return imageInfo.getName();
        case COLUMN_WIDTH:
            return imageInfo.getWidth();
        case COLUMN_HEIGHT:
            return imageInfo.getHeight();
        case COLUMN_THUMBNAIL:
            return imageInfo.getThumbnail();
        case COLUMN_CONTROLS:
            return "";
        }
        return "???";
    }

    public String getColumnName(int column) {
        return COLUMN_TITLES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case COLUMN_NUMBER:
        case COLUMN_HEIGHT:
        case COLUMN_WIDTH:
            return Integer.class;
        case COLUMN_THUMBNAIL:
            return ImageIcon.class;
        default:
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == COLUMN_CONTROLS);
    }
}
