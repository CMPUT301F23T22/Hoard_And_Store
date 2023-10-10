// extends Shape class, Pentagon class is used to create a pentagon shape branch.
// A pentagon shape branch is a branch that has 5 sides s.

Class Pentagon extends Shape {
    private int s;

    public Pentagon(int x, int y, int s) {
        super(x, y);
        this.s = s;
    }

    @Override
    public void draw() {
        System.out.println();
    }
}