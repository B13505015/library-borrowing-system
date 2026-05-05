package library_api.dto;

public class FinePolicyRequest {
    private String roleLevel;
    private double overdueFinePerDay;
    private int fineGraceDays;

    public String getRoleLevel() { return roleLevel; }
    public void setRoleLevel(String roleLevel) { this.roleLevel = roleLevel; }
    public double getOverdueFinePerDay() { return overdueFinePerDay; }
    public void setOverdueFinePerDay(double overdueFinePerDay) { this.overdueFinePerDay = overdueFinePerDay; }
    public int getFineGraceDays() { return fineGraceDays; }
    public void setFineGraceDays(int fineGraceDays) { this.fineGraceDays = fineGraceDays; }
}
