public class PersonalInsurance extends RentalInsurance {
    public PersonalInsurance(String id) {
        super(id);
    }

    @Override
    public boolean addInsurance() {
        System.out.println("Personal insurance added");
        return true;
    }
}
