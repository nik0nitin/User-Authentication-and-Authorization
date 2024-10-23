package practice;

public class PassingMethodAsValue {
    private static final String name = new String("Nitin");

    public static String getName(){
        return name;
    }

    public String mergeString(String name, String surname){
        String fullName=name + " " + surname;
        return fullName;
    }

    public static void main(String[] args) {
        PassingMethodAsValue p = new PassingMethodAsValue();
        String fullName =  p.mergeString(PassingMethodAsValue.getName(), "Jaswani");
        System.out.println(fullName);

    }
}
