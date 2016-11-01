
package utils;

import java.util.List;
import java.util.ListIterator;

public class Matrix {

    private class Matr {

        double _11, _12, _13;
        double _21, _22, _23;
        double _31, _32, _33;

        Matr() {
            _11 = 0.0;
            _12 = 0.0;
            _13 = 0.0;
            _21 = 0.0;
            _22 = 0.0;
            _23 = 0.0;
            _31 = 0.0;
            _32 = 0.0;
            _33 = 0.0;
        }
    }
    private Matr matr = new Matr();

    public Matrix() {
        //initialize the matrix to an identity matrix
        identity();
    }

    public void _11(double val) {
        matr._11 = val;
    }

    public void _12(double val) {
        matr._12 = val;
    }

    public void _13(double val) {
        matr._13 = val;
    }

    public void _21(double val) {
        matr._21 = val;
    }

    public void _22(double val) {
        matr._22 = val;
    }

    public void _23(double val) {
        matr._23 = val;
    }

    public void _31(double val) {
        matr._31 = val;
    }

    public void _32(double val) {
        matr._32 = val;
    }

    public void _33(double val) {
        matr._33 = val;
    }

//multiply two matrices together
    private void matrixMultiply(Matr mIn) {
        Matr mat_temp = new Matr();

        //first row
        mat_temp._11 = (matr._11 * mIn._11) + (matr._12 * mIn._21) + (matr._13 * mIn._31);
        mat_temp._12 = (matr._11 * mIn._12) + (matr._12 * mIn._22) + (matr._13 * mIn._32);
        mat_temp._13 = (matr._11 * mIn._13) + (matr._12 * mIn._23) + (matr._13 * mIn._33);

        //second
        mat_temp._21 = (matr._21 * mIn._11) + (matr._22 * mIn._21) + (matr._23 * mIn._31);
        mat_temp._22 = (matr._21 * mIn._12) + (matr._22 * mIn._22) + (matr._23 * mIn._32);
        mat_temp._23 = (matr._21 * mIn._13) + (matr._22 * mIn._23) + (matr._23 * mIn._33);

        //third
        mat_temp._31 = (matr._31 * mIn._11) + (matr._32 * mIn._21) + (matr._33 * mIn._31);
        mat_temp._32 = (matr._31 * mIn._12) + (matr._32 * mIn._22) + (matr._33 * mIn._32);
        mat_temp._33 = (matr._31 * mIn._13) + (matr._32 * mIn._23) + (matr._33 * mIn._33);

        matr = mat_temp;
    }

    //applies a 2D transformation matrix to a std::vector of Vector2Ds
    public void transformVector2Ds(List<Vector> vPoint) {
        ListIterator<Vector> it = vPoint.listIterator();
        while (it.hasNext()) {
            Vector i = it.next();
            double tempX = (matr._11 * i.x) + (matr._21 * i.y) + (matr._31);
            double tempY = (matr._12 * i.x) + (matr._22 * i.y) + (matr._32);
            i.x = tempX;
            i.y = tempY;
        }
    }

    //applies a 2D transformation matrix to a single Vector2D
    public void transformVector2Ds(Vector vPoint) {
        double tempX = (matr._11 * vPoint.x) + (matr._21 * vPoint.y) + (matr._31);
        double tempY = (matr._12 * vPoint.x) + (matr._22 * vPoint.y) + (matr._32);
        vPoint.x = tempX;
        vPoint.y = tempY;
    }

    //create an identity matrix
    public void identity() {
        matr._11 = 1;
        matr._12 = 0;
        matr._13 = 0;
        matr._21 = 0;
        matr._22 = 1;
        matr._23 = 0;
        matr._31 = 0;
        matr._32 = 0;
        matr._33 = 1;
    }

    //create a transformation matrix
    public void translate(double x, double y) {
        Matr mat = new Matr();
        mat._11 = 1; mat._12 = 0; mat._13 = 0;        
        mat._21 = 0; mat._22 = 1; mat._23 = 0;       
        mat._31 = x; mat._32 = y; mat._33 = 1;
        //and multiply
        matrixMultiply(mat);
    }

    //create a scale matrix
    public void scale(double xScale, double yScale) {
        Matr mat = new Matr();
        mat._11 = xScale; mat._12 = 0; mat._13 = 0;
        mat._21 = 0; mat._22 = yScale; mat._23 = 0;
        mat._31 = 0; mat._32 = 0; mat._33 = 1;
        //and multiply
        matrixMultiply(mat);
    }

    //create a rotation matrix
    public void rotate(double rot) {
        Matr mat = new Matr();
        double Sin = Math.sin(rot);
        double Cos = Math.cos(rot);
        mat._11 = Cos; mat._12 = Sin; mat._13 = 0;
        mat._21 = -Sin; mat._22 = Cos; mat._23 = 0;
        mat._31 = 0; mat._32 = 0; mat._33 = 1;
        //and multiply
        matrixMultiply(mat);
    }

    //create a rotation matrix from a 2D vector
    public void rotate(Vector fwd, Vector side) {
        Matr mat = new Matr();
        mat._11 = fwd.x; mat._12 = fwd.y;  mat._13 = 0;
        mat._21 = side.x; mat._22 = side.y; mat._23 = 0;
        mat._31 = 0;mat._32 = 0;mat._33 = 1;
        //and multiply
        matrixMultiply(mat);
    }
    
}