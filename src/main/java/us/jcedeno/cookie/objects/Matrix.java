package us.jcedeno.cookie.objects;

import lombok.Data;

/**
 * A matrix generic object to easily manage matrices of data.
 * 
 * @author jcedeno
 */
public @Data class Matrix<T> {
    volatile T[][] matrix;

    @SuppressWarnings("unchecked")
    public Matrix(int rows, int columns) {
        this.matrix = (T[][]) new Object[rows][columns];
    }

    /**
     * Returns the object at the specified position.
     * 
     * @param row    The row of the object.
     * @param column The column of the object.
     * @return The object at the specified position.
     */
    public T getAtRowAndColumn(int row, int column) {
        return matrix[row][column];
    }

    /**
     * Sets the object at the specified position.
     * 
     * @param row    The row of the object.
     * @param column The column of the object.
     * @param object The object to set.
     * @return The object at the specified position.
     */
    public T setAtRowAndColumn(int row, int column, T object) {
        return matrix[row][column] = object;
    }

}
