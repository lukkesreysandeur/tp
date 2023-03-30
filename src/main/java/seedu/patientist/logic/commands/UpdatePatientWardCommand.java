package seedu.patientist.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.patientist.logic.parser.CliSyntax.PREFIX_WARD;

import java.util.List;

import seedu.patientist.commons.core.Messages;
import seedu.patientist.commons.core.index.Index;
import seedu.patientist.logic.commands.exceptions.CommandException;
import seedu.patientist.model.Model;
import seedu.patientist.model.Patientist;
import seedu.patientist.model.person.Person;
import seedu.patientist.model.person.patient.Patient;
import seedu.patientist.model.ward.Ward;

/**
 * Update Patient's ward identified using it's displayed index from the patientist book.
 */

public class UpdatePatientWardCommand extends Command {
    public static final String COMMAND_WORD = "trfWard";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Update the patient ward from one ward to another ward "
            + "Parameters: "
            + "Index " + PREFIX_WARD + "WARD " + PREFIX_WARD + "WARD "
            + "Example: " + COMMAND_WORD + " "
            + "2 " + PREFIX_WARD + "Block B Ward 2 " + PREFIX_WARD + "Block C Ward 1";

    public static final String MESSAGE_SUCCESS = "Patient %1$s has been transferred from ward %2$s to ward %3$s";
    public static final String MESSAGE_WARD_NOT_FOUND = "Ward not found: %1$s";
    public static final String MESSAGE_WARD_INCORRECT = "Ward of patient is incorrect";

    private final String ogWard;
    private final String nextWard;
    private final Index patient;

    /**
     * Creates an UpdatePatientWardCommand to change specified {@code Index} ward {@code ogWard} to
     * {@code nextWard}.
     */
    public UpdatePatientWardCommand(Index patient, String ogWard, String nextWard) {
        this.ogWard = ogWard;
        this.nextWard = nextWard;
        this.patient = patient;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasWard(new Ward(ogWard))) {
            throw new CommandException(String.format(MESSAGE_WARD_NOT_FOUND, ogWard));
        }

        if (!model.hasWard(new Ward(nextWard))) {
            throw new CommandException(String.format(MESSAGE_WARD_NOT_FOUND, nextWard));
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        if (patient.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Patient personToBeUpdated = (Patient) lastShownList.get(patient.getZeroBased());
        Patientist patientist = (Patientist) model.getPatientist();
        try {
            patientist.transferPatient(personToBeUpdated, model.getWard(ogWard), model.getWard(nextWard));
        } catch (Exception e) {
            throw new CommandException(MESSAGE_WARD_INCORRECT);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, patient.getOneBased(), ogWard, nextWard));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UpdatePatientWardCommand
                && patient.equals(((UpdatePatientWardCommand) other).patient)
                && ogWard.equals(((UpdatePatientWardCommand) other).ogWard)
                && nextWard.equals(((UpdatePatientWardCommand) other).nextWard)); // instanceof handles nulls
    }
}
