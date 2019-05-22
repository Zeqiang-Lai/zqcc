int global_var1;
double global_var2;

void func(int a, int b)
{
    int c = a+(b+c);
    return;
}

double func3(double a, int b)
{
    a = (double)b;
    return a;
}

int main()
{
    int a[10], b[10][10+2];
    a[1] = 5;
    b[4][2] = 10;
    int c, d;

    func(c,d);
    double x = 0.9;
    double y = func3(x, d);
    return 0;
}