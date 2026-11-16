import java.sql.Timestamp;

public class TempRecord {

    private int id;
    private double inputValue;
    private double resultValue;
    private int fromUnitId;
    private int toUnitId;
    private String language;
    private Timestamp createdAt;

    public TempRecord(double inputValue, double resultValue,
                      int fromUnitId, int toUnitId, String language, Timestamp createdAt) {
        this.inputValue = inputValue;
        this.resultValue = resultValue;
        this.fromUnitId = fromUnitId;
        this.toUnitId = toUnitId;
        this.language = language;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public double getInputValue() {
        return inputValue;
    }

    public double getResultValue() {
        return resultValue;
    }

    public int getFromUnitId() {
        return fromUnitId;
    }

    public int getToUnitId() {
        return toUnitId;
    }

    public String getLanguage() {
        return language;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

}