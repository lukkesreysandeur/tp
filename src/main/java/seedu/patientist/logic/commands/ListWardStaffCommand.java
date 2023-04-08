package seedu.patientist.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.patientist.commons.core.Messages;
import seedu.patientist.logic.commands.exceptions.CommandException;
import seedu.patientist.model.Model;
import seedu.patientist.model.person.staff.StaffInWardPredicate;
import seedu.patientist.model.ward.Ward;

/**
 * Returns all the staffs that are tag to a particular ward
 */
public class ListWardStaffCommand extends Command {
    public static final String COMMAND_WORD = "lswardstf";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all staff whose wards matches any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Keywords must be alphanumeric.\n"
            + "Parameters: KEYWORD WARD_NAME\n"
            + "Example: " + COMMAND_WORD + " Block 1 Ward A";

    public static final String MESSAGE_WARD_NOT_FOUND = "Ward [%1$s] not found.";

    private final String keyword;

    public ListWardStaffCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasWard(new Ward(keyword))) {
            throw new CommandException(String.format(MESSAGE_WARD_NOT_FOUND, keyword));
        }

        StaffInWardPredicate predicate = new StaffInWardPredicate(model, keyword);

        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()),
                -1, -1, false, false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ListWardStaffCommand // instanceof handles nulls
                && keyword.equals(((ListWardStaffCommand) other).keyword));
    }
}