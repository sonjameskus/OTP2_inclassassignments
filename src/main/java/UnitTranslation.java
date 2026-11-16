public class UnitTranslation {
    private int unitId;
    private String lang;
    private String name;
    private String desc;

    public UnitTranslation(int unitId, String lang, String name, String desc) {
        this.unitId = unitId;
        this.lang = lang;
        this.name = name;
        this.desc = desc;
    }

    public int getUnitId() {
        return unitId;
    }
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
