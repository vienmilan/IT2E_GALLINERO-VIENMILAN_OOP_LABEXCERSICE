public class ShapeTester{
    public static void main(String[] args) {
        
        Shape circle = new Circle(6.0f);
        System.out.println(circle.DisplayInfo());  

        Shape rectangle = new Rectangle(2.0f, 5.0f);
        System.out.println(rectangle.DisplayInfo());

        Shape square = new Square(7.0f);
        System.out.println(square.DisplayInfo());

        Shape triangle = new Triangle(8.0f, 4.0f, 7.0f);
        System.out.println(triangle.DisplayInfo());

    }
}
