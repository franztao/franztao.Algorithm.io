package structuralProxyPattern;

public class ProxyImage implements Image
{
    private RealImage realImage;
    private String fileName;

    public ProxyImage(String string)
    {
        this.fileName = string;
    }

    @Override
    public void display()
    {
        if (realImage == null)
        {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }

}
