package formatfa.qqgroupdroid.Tool;

public class LogTool {
    private StringBuilder stringBuilder;
    private String split="\n";

    public LogTool() {
        stringBuilder = new StringBuilder();

    }
    public void init()
    {
        stringBuilder = new StringBuilder();
    }
    public void add(String msg)
    {
        stringBuilder.append(msg);
        stringBuilder.append(split);
    }
    public void setSplit(String split)
    {
        this.split = split;
    }
    public String toString()
    {
        return  stringBuilder.toString();
    }
}
