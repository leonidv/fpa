package ru.spb.leonidv.flashpresentation.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ru.spb.leonidv.flashpresentation.presentation.Presentation;

class RowControlEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private static final long serialVersionUID = -1696999921113280673L;

    RowControlPanel controlPanel;

    public RowControlEditor(Presentation presentation) {
        controlPanel = new RowControlPanel(presentation);
    }

    public Object getCellEditorValue() {
        return null;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
            int row, int column) {
        if (isSelected) {
            controlPanel.setIndex(row);
            return controlPanel;
        } else {
            return null;
        }

    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        if (isSelected) {
            return controlPanel;
        } else {
            return null;
        }
    }

    /** ********************************************************* */
    private class RowControlPanel extends JPanel {
        private static final long serialVersionUID = -6500412630072880342L;

        // Кнопка, отвечающая за перемещние строки вверх
        private JButton buttonUp = new JButton();;

        // Кнопка, отвечающая за перемещение строки вниз
        private JButton buttonDown = new JButton();

        // Кнопка, отвечающая за удалением из списка
        private JButton buttonRemove = new JButton();

        // Презентация, которая обрабатывается панелью управления.
        private Presentation presentation;

        // Номер элемента, для которого отображается компонент
        private int index;

        public RowControlPanel(Presentation presentation) {
            this.presentation = presentation;
            initGUI();
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        private void initGUI() {
            try {
                BoxLayout thisLayout = new BoxLayout(this, javax.swing.BoxLayout.X_AXIS);
                setLayout(thisLayout);
                setFocusable(false);
                setMaximumSize(new Dimension(100, 30));

                buttonUp.setText("\u2191");
                buttonUp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        moveUp();
                    }
                });
                add(buttonUp);

                buttonDown.setText("\u2193");
                buttonDown.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        moveDown();
                    }
                });
                add(buttonDown);

                buttonRemove.setText("X");
                buttonRemove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        remove();
                    }
                });
                add(buttonRemove);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void moveUp() {
            if ((index > 0) && (index < presentation.size())) {
                presentation.move(index, index - 1);
            }
        }

        private void moveDown() {
            if ((index >= 0) && (index < (presentation.size() - 1))) {
                presentation.move(index, index + 1);
            }
        }

        private void remove() {
            if ((index >= 0) && (index < (presentation.size()))) {
                presentation.remove(index);
            }
        }

    }

}