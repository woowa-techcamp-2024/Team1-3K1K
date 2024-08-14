package camp.woowak.lab.customer.service.command;

public record SignUpCustomerCommand(String name, String email, String password, String phone) {
}
