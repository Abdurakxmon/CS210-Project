public class LiabilityInsurance extends RentalInsurance {
    public LiabilityInsurance(String id) {
        super(id);
    }

    @Override
    public boolean addInsurance() {
        System.out.println("Liability insurance added");
        return true;
    }
}
