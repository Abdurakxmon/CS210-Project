public class BelongingInsurance extends RentalInsurance {
    public BelongingInsurance(String id) {
        super(id);
    }

    @Override
    public boolean addInsurance() {
        System.out.println("Belonging insurance added");
        return true;
    }
}
