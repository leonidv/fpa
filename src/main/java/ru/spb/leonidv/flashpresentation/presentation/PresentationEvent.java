package ru.spb.leonidv.flashpresentation.presentation;

/**
 * Интерфейс определяет действия, которые вызываются на разных этапах загрузки изображений.
 * 
 * @author Leonid
 * 
 */
public interface PresentationEvent {
    /**
     * Вызывается при начале загрузке изображений из папки.
     * 
     * @param count
     *            - количество изображений, которое будет добавлено в презентацию.
     */
    void startLoading(int count);

    /**
     * Вызывается при окончании загрузки очередного изображения.
     * 
     * @param index
     *            - номер обработанного изображения
     * @param imageInfo
     *            - информация о загруженном изображении.
     */
    void imageLoaded(int index, ImageInfo imageInfo);

    /**
     * Вызывает при перемещение изображения с одной позиции на другую
     * 
     * @param source
     *            - индекс элемента до перемещения
     * @param dest
     *            - индекс элемента после перемещения
     */
    void move(int source, int dest);

    /**
     * Вызывается при удалении изображения из презентации.
     * 
     * @param index
     *            - индекс удаляемого элемента
     */
    void remove(int index);

}