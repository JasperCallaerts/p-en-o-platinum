package internal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Martijn on 20/08/2017.
 * A class of matrices provided with some of the most basic matrix computations
 */

public class MathMatrix<T> {

    /*
    Constructors
     */
    /**
     * Public constructor using a matrixList and the shape of the matrix
     * @param matrixList a list containing the matrix elements
     * @param nRows the amount of rows in the matrix
     * @param nColumns the amount of columns in the matrix
     */
    public MathMatrix(List<T> matrixList, int nRows, int nColumns) throws IllegalArgumentException{
        if(isValidShape(matrixList, nRows, nColumns)){
            this.setMatrixList(matrixList);
            this.setnRows(nRows);
            this.setnColumns(nColumns);
        }else{
            throw new IllegalArgumentException(INVALID_SHAPE);
        }
    }

    public MathMatrix(List<List<T>> listOfListMatrix){
        //get the shape of the 2D list
        this.setnRows(listOfListMatrix.size());
        this.setnColumns(listOfListMatrix.get(0).size());

        List<T> tempMatrixList = new ArrayList<T>();
        //generate the temporary matrixList
        for(List<T> Row: listOfListMatrix){
            tempMatrixList.addAll(Row);
        }
        this.setMatrixList(tempMatrixList);
    }

    public MathMatrix(){
        this.setMatrixList(new ArrayList<>());
        this.setnRows(0);
        this.setnColumns(0);
    }

    /*
    Instance Methods: Index Operations
     */

    /*
    Individual Element Operations
     */

    /**
     * Returns the element at position (row, column) in the matrix
     * @param row the row to be selected in the matrix
     * @param column the column to be selected in the matrix
     */
    public T getElementByIndex(int row, int column){
        if(!isValidIndex(row, column)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }

        int matrixListIndex = fromCoordinateToIndex(row, column);
        return this.getMatrixList().get(matrixListIndex);
    }

    /**
     * Returns the matrix element at the given index as if the matrix was flattened out row per row
     * with the corresponding coordinates: (index/this.getnColumns(), index%this.getnColumns())
     * @param index the index in the flattened matrix
     */
    public T getElementByIndex(int index){
        if(!isValidIndex(index)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }
        return this.getMatrixList().get(index);
    }

    /**
     * Sets the value of the matrix element located at (row, column) to the desired element
     * @param element the desired value
     * @param row the row of the element in the matrix
     * @param column the column of the element in the matrix
     */
    public void setElementByIndex(T element, int row, int column){
        if(!isValidIndex(row, column)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }

        int matrixListIndex = fromCoordinateToIndex(row, column);
        this.getMatrixList().set(matrixListIndex, element);
    }

    /**
     * Sets the value of the matrix element in the given index as if the matrix was flattened out
     * row per row with the corresponding coordinates:
     * (index/this.getnColumns(), index%this.getnColumns())
     * @param element the desired value to be set
     * @param index the index in the flattened matrix
     */
    public void setElementByIndex(T element, int index){
        if(!isValidIndex(index)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }
        this.getMatrixList().set(index, element);
    }

    /**
     * Transforms a 2D coordinate to a flattened index for calculations on the listMatrix
     * @param row the desired row in the 2D coordinate system
     * @param column the desired column in the 2D coordinate system
     * @return result = row*this.getnColumns() + column
     */
    public int fromCoordinateToIndex(int row, int column){
        return row*this.getnColumns()+column;
    }

    /*
    Multiple element operations
     */

    /**
     * Returns a copy of the given row in the matrix
     * @param row the selected row
     * @return a copy of the given row in the matrix
     * @throws IllegalArgumentException if the index of the row is out of bounds
     */
    public List<T> getRowAtIndex(int row) throws IllegalArgumentException{
        if(!between(row, 0, this.getnRows())){
            throw new IllegalArgumentException(INVALID_INDEX);
        }
        int startIndex = row*this.getnColumns();
        int endIndex = row*(this.getnColumns())+ this.getnColumns();

        List<T> tempRow = new ArrayList<T>(this.getMatrixList().subList(startIndex, endIndex));
        return tempRow;
    }

    /**
     * Returns the selected column of the matrix
     * @param column the desired column
     * @return the selected column of the matrix
     * @throws IllegalArgumentException if the column index is out of bounds
     */
    public List<T> getColumnAtIndex(int column) throws IllegalArgumentException{
        if(!between(column, 0, this.getnColumns())){
            throw new IllegalArgumentException(INVALID_INDEX);
        }

        List<T> tempColumn = new ArrayList<T>();
        for(int i = 0; i != this.getnRows(); i++){
            T currenElem = this.getElementByIndex(i, column);
            tempColumn.add(currenElem);
        }
        return tempColumn;
    }

    /**
     * Inserts a new row at the given row-index in the matrix
     * @param elementRow the row to be inserted
     * @param row the row where the row of data needs to be inserted
     */
    public void insertRowAtIndex(List<T> elementRow, int row){
        if(!between(row, 0, this.getnRows()+1)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }else if(elementRow.size()!=this.getnColumns()&&this.getnColumns()!=0){
            throw new IllegalArgumentException(INVALID_SIZE);
        }

        int tempIndex = fromCoordinateToIndex(row, 0);
        this.getMatrixList().addAll(tempIndex, elementRow);

        this.setnRows(this.getnRows() + 1);
        if(this.getnColumns()==0){
            this.setnColumns(elementRow.size());
        }
    }

    /**
     * Inserts a new Column at the given column-index in the matrix
     */
    public void insertColumnAtIndex(List<T> elementColumn, int column){
        if(!between(column, 0, this.getnColumns()+1)){
            throw new IllegalArgumentException(INVALID_INDEX);
        }else if(elementColumn.size()!=this.getnRows()&&this.getnRows()!=0){
            throw new IllegalArgumentException(INVALID_SIZE);
        }

        int prevAmountCols = this.getnColumns();

        for(int rowIndex = elementColumn.size()-1; rowIndex != -1; rowIndex--){
            int indexInMatrix = rowIndex*prevAmountCols + column;
            this.getMatrixList().add(indexInMatrix, elementColumn.get(rowIndex));
        }
        this.setnColumns(this.getnColumns()+1);
        if(this.getnRows()==0){
            this.setnRows(elementColumn.size());
        }
    }

    /**
     * Selects a matrix slice from the given matrix(copy), with the following boundaries
     * the start of the rows startRow(inclusive) and the end of the rows(exclusive)
     * the start of the columns startColumn(inclusive) and the end of the rows(exclusive)
     * @param startRow start boundary of the rows of the matrix slice
     * @param endRow end boundary of the rows of the matrix slice
     * @param startColumn start boundary of the columns of the matrix slice
     * @param endColumn end boundary of the columns of the matrix slice
     * @return a copy of the slice of the matrix
     */
    public MathMatrix<T> getMatrixSlice(int startRow, int endRow, int startColumn, int endColumn){
        if(!isValidSlice(startRow, endRow, startColumn, endColumn)){
            throw new IllegalArgumentException(INVALID_SLICE);
        }
        MathMatrix<T> tempMatrix = new MathMatrix<T>();
        for(int rowIndex = startRow; rowIndex != endRow; rowIndex++){
            List<T> tempRow = this.getRowAtIndex(rowIndex);
            List<T> tempRowSlice = new ArrayList<T>(tempRow.subList(startColumn, endColumn));
            tempMatrix.insertRowAtIndex(tempRowSlice, rowIndex);
        }

        return tempMatrix;
    }

    /*
    Instance Methods: Math
     */

    public static MathMatrix<Float> getRollTransformMatrix(float angle){
        Float[] elemArray = new Float[]{(float) Math.cos(angle), -  (float)Math.sin(angle), 0f,
                                        (float)Math.sin(angle),  (float) Math.cos(angle),   0f,
                                        0f,                      0f,                        1f};
        ArrayList<Float> elemList = new ArrayList<Float>();
        elemList.addAll(Arrays.asList(elemArray));


        return new MathMatrix<Float>(elemList, 3,3);

    }

    public static MathMatrix<Float> getPitchTransformMatrix(float angle){
        Float[] elemArray = new Float[]{1f, 0f,                      0f,
                                        0f, (float) Math.cos(angle), - (float)Math.sin(angle),
                                        0f, (float)Math.sin(angle),  (float) Math.cos(angle)};
        ArrayList<Float> elemList = new ArrayList<Float>();
        elemList.addAll(Arrays.asList(elemArray));


        return  new MathMatrix<Float>(elemList, 3,3);

    }

    public static MathMatrix<Float> getHeadingTransformMatrix(float angle){
        Float[] elemArray = new Float[]{(float) Math.cos(angle),   0f, (float)Math.sin(angle),
                                         0f,                       1f,  0f
                                         - (float)Math.sin(angle), 0f, (float) Math.cos(angle)};
        ArrayList<Float> elemList = new ArrayList<Float>();
        elemList.addAll(Arrays.asList(elemArray));


        return  new MathMatrix<Float>(elemList, 3,3);

    }

    /**
     * Converts a 3*1 matrix to a 3d Vector of type float
     * @return a Vector
     */
    public Vector convertToVector(){
        if(!canBeConvertedToVector()){
            throw new IllegalArgumentException(VECTOR_ERROR);
        }

        List<T> tempList = this.getRowAtIndex(0);

        try{
            float x_part = (float) tempList.get(0);
            float y_part = (float) tempList.get(1);
            float z_part = (float) tempList.get(2);

            return new Vector(x_part, y_part, z_part);

        }catch(ClassCastException e){
            throw new IllegalStateException(VECTOR_ELEM);
        }


    }


    //Todo: implement this one
    public Boolean canBeConvertedToVector(){
        return true;
    }




    /**
     * Returns the sum of this and the other matrix, if the type of the matrix is addable
     * @param other the other matrix to sum this matrix instance with
     * @throws IllegalArgumentException if the type of the matrix is not addable (see enum MathType)
     */
    public void matrixSum(MathMatrix<T> other) throws IllegalArgumentException{
        if(!canBeAdded(this, other)){
            throw new IllegalArgumentException(SHAPE_MISMATCH);
        }
        List<T> matrixListOther = other.getMatrixList();

        int matrixSize = matrixListOther.size();

        for(int index = 0; index != matrixSize; index++) {
            T thisCurrentElement = this.getElementByIndex(index);
            T otherCurrentElement = other.getElementByIndex(index);
            try {
                T sum = (T) addElement(thisCurrentElement, otherCurrentElement);
                this.setElementByIndex(sum, index);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    public MathMatrix<T> transpose(){
        int matrixSize = this.getMatrixList().size();
        MathMatrix<T> tempMatrix = new MathMatrix<T>();
        List<T> tempColumn;
        for(int index = 0; index != this.getnRows(); index++){
            tempColumn = this.getRowAtIndex(index);
            tempMatrix.insertColumnAtIndex(tempColumn, index);
        }

        return tempMatrix;
    }

    /**
     * Performs a matrix product on two matrices
     * @param other the other matrix in the product
     * @return a matrix containing the matrixproduct of the two matrices
     */
    public MathMatrix<T> matrixProduct(MathMatrix<T> other){
        int nbResCols = other.getnColumns();
        int nbResRows = this.getnRows();
        List<T> tempRow;
        List<T> tempColumn;
        List<T> tempMatrixList = new ArrayList<T>();
        T tempElem;
        for(int i = 0; i != nbResRows; i++){
            for(int j = 0; j != nbResCols; j++){

                tempRow = this.getRowAtIndex(i);
                tempColumn = other.getColumnAtIndex(j);
                tempElem = scalarProduct(tempRow, tempColumn);
                tempMatrixList.add(tempElem);
            }
        }

        return new MathMatrix<T>(tempMatrixList, nbResRows, nbResCols);
    }

    /*
    Checkers for instances
     */

    /**
     * Checks if the given coordinate index is valid
     */
    public boolean isValidIndex(int row, int column){
        return between(row, 0, this.getnRows())&&between(column, 0, this.getnColumns());
    }

    /**
     * Checks if the given index is valid
     */
    public boolean isValidIndex(int index){
        return between(index, 0, this.getMatrixList().size());
    }

    /**
     * Checks if a slice is valid;
     * @param startRow the start row of the slice (inclusive)
     * @param endRow the end row of the slice (exclusive)
     * @param startColumn  the start column of the slice (inclusive)
     * @param endColumn the end column of the slice (exclusive)
     * @return true if and only if the slice is valid
     *          |result == (0 <= startRow < endRow <= this.getnRows() + 1)&&
     *          |          (0 <= startColumn < endColumn <= this.getnColumns() + 1 )
     */
    public boolean isValidSlice(int startRow, int endRow, int startColumn, int endColumn){
        int checks = 0;
        int neededChecks = 6;

        // checkings to do:
        checks += (startRow < endRow ? 1 : 0);
        checks += (startRow >= 0 ? 1 : 0 );
        checks += (endRow <= this.getnRows() + 1 ? 1 : 0);
        checks += (startColumn < endColumn ? 1 : 0);
        checks += (startColumn >= 0 ? 1 : 0);
        checks += (endColumn <= this.getnColumns() + 1 ? 1 : 0);

        // if not all the checks are positive return false, else return true
        return checks == neededChecks;
    }

    /**
     * Two matrices of the same type can be added if they have the same shape
     * @param matrix1 the first matrix to be added
     * @param matrix2 the second matrix to be added
     * @return true if and only if the two matrices have the same shape
     */
    public boolean canBeAdded(MathMatrix<T> matrix1, MathMatrix<T> matrix2){

        return (matrix1.getnRows()==matrix2.getnRows())&&(matrix1.getnColumns()==matrix2.getnColumns());
    }

    /**
     * Checks if the two given list are the same size
     * @param list1 the first list to check
     * @param list2 the second list to check
     * @return true if and only if the two lists have the same size
     */
    private static boolean hasSameSize(List list1, List list2){
        return  list1.size() == list2.size();
    }

    /*
    Static methods
     */
    /**
     * Checks whether the shape can be created from the given matrixList
     * @param matrixList the list containing the elements of the matrix
     * @param nRows the amount of rows of the matrix
     * @param nColumns the amount of columns of the matrix
     * @return  true if and only if a matrix of such a shape can be created
     *          |result = matrixList.size() == nRows*nColumns
     */
    public static boolean isValidShape(List matrixList, int nRows, int nColumns){
        return matrixList.size() == nRows*nColumns;
    }

    /**
     * Checks if the given integer value is between border1(inclusive) and border2(exclusive)
     */
    public static boolean between(int value, int border1, int border2){
        return (value >= border1 && value < border2);
    }

    public Object addElement(T object1, T object2){
        MathType type;
        try {
            type = MathType.valueOf(object1.getClass().getSimpleName());
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(INVALID_ADD_TYPES);
        }

        switch(type){

            case Integer:
                return (Integer)object1 + (Integer)object2;

            case Long:
                return (Long)object1 + (Long)object2;

            case Float:
                return (Float)object1 + (Float)object2;

            case Double:
                return (Double)object1 + (Double)object2;

            default:
                throw new IllegalArgumentException("nothing");
        }

    }

    public Object multiplyElement(T object1, T object2){
        MathType type;
        try {
            type = MathType.valueOf(object1.getClass().getSimpleName());
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(INVALID_ADD_TYPES);
        }

        switch(type){

            case Integer:
                return (Integer)object1 * (Integer)object2;

            case Long:
                return (Long)object1 * (Long)object2;

            case Float:
                return (Float)object1 * (Float)object2;

            case Double:
                return (Double)object1 * (Double)object2;

            default:
                throw new IllegalArgumentException("nothing");
        }
    }

    enum MathType {
        Integer, Long, Float, Double
    }

    /**
     * Checks if a matrix multiplication is possible between two same typed matrices
     * @param matrix1 first matrix
     * @param matrix2 second matrix
     * @return true if and only if matrix1 has n columns and matrix2 has n rows
     */
    public boolean canBeMultiplied(MathMatrix<T> matrix1, MathMatrix<T> matrix2){

        return matrix1.getnColumns() == matrix2.getnRows();
    }

    /**
     * Calculates a scalar product of the two lists
     * @param list1
     * @param list2
     * @return
     */
    private T scalarProduct(List<T> list1, List<T> list2){
        if(!hasSameSize(list1, list2)){
            throw new IllegalArgumentException();
        }
        T result = list1.get(0);
        int listSize = list1.size();


        for(int index = 0; index != listSize; index++) {

            if (index == 0)
                result = (T) multiplyElement(list1.get(index), list2.get(index));
            else
                result = (T) this.addElement(result, (T) multiplyElement(list1.get(index), list2.get(index)));
        }

        return result;
    }

    /*
    Error messages
     */
    public final static String INVALID_SHAPE = "A matrix of such shape cannot be created with the given matrixList";
    public final static String INVALID_INDEX = "The index is out of bounds for the given matrix";
    public final static String INVALID_SIZE = "The size of the column or row to be inserted does not correspond to the matrix shape";
    public final static String INVALID_SLICE = "The given indices for the slice are invalid";
    public final static String SHAPE_MISMATCH = "The shape of the two matrices are not the same";
    public final static String INVALID_ADD_TYPES = "The type of the given matrix is not addable";
    public final static String VECTOR_ERROR = "the matrix cannot be converted to a vector of given size";
    public final static String VECTOR_ELEM = "the type of the matrix is not suited for a vector";

    /*
    Instance variables
     */

    /**
     * Variable that stores the matrix list
     */
    private List<T> matrixList = new ArrayList<T>();

    /**
     * Variable that stores the number of rows in the matrix
     */
    private int nRows;

    /**
     * Variable that stores the number of columns in the matrix
     */
    private int nColumns;

    /**
     * Getters and setters of the instance variables
     */

    /**
     * basic getter for the matrixList
     * @return
     */
    private List<T> getMatrixList() {
        return matrixList;
    }

    /**
     * basic setter for the matrixList
     * @param matrixList
     */
    private void setMatrixList(List<T> matrixList) {
        this.matrixList.addAll(matrixList);
    }

    /**
     * basic getter for the number of rows of the matrix
     * @return
     */
    public int getnRows() {
        return nRows;
    }

    /**
     * basic setter for the number of rows of the matrix
     * @param nRows
     */
    private void setnRows(int nRows) {
        this.nRows = nRows;
    }

    /**
     * basic getter for the number of columns of the matrix
     * @return
     */
    public int getnColumns() {
        return nColumns;
    }


    /**
     * basic setter for the number of columns of the matrix
     * @param nColumns
     */
    private void setnColumns(int nColumns) {
        this.nColumns = nColumns;
    }


}

