package seedu.patientist.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.patientist.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.patientist.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.patientist.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.patientist.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.patientist.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.patientist.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.patientist.testutil.TypicalPatients.AMY;
import static seedu.patientist.testutil.TypicalPatients.CHARLIE;
import static seedu.patientist.testutil.TypicalWards.getTypicalPatientist;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.patientist.commons.core.Messages;
import seedu.patientist.commons.core.index.Index;
import seedu.patientist.model.Model;
import seedu.patientist.model.ModelManager;
import seedu.patientist.model.Patientist;
import seedu.patientist.model.UserPrefs;
import seedu.patientist.model.person.patient.Patient;
import seedu.patientist.model.person.patient.PatientStatusDetails;
import seedu.patientist.testutil.PatientBuilder;

public class AddPatientStatusCommandTest {
    private Model model = new ModelManager(getTypicalPatientist(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        List<PatientStatusDetails> newStatus = List.of(new PatientStatusDetails("Feeling ok"));
        AddPatientStatusCommand addCommand = new AddPatientStatusCommand(INDEX_SECOND_PERSON, newStatus);

        Patient editedPatient = new PatientBuilder(AMY).withStatus("Doing fine", "Feeling ok").build();

        String expectedMessage =
                String.format(AddPatientStatusCommand.MESSAGE_ADD_STATUS_SUCCESS, newStatus, editedPatient);

        Model expectedModel = new ModelManager(getTypicalPatientist(), new UserPrefs());
        Patientist patientist = (Patientist) expectedModel.getPatientist();
        patientist.removePerson(expectedModel.getFilteredPersonList().get(1));
        expectedModel.addPatient(editedPatient, expectedModel.getWard("Block A Ward 1"));
        expectedModel.getPatientist().updatePersonList();

        assertCommandSuccess(addCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_THIRD_PERSON);

        List<PatientStatusDetails> newStatus = List.of(new PatientStatusDetails("Feeling ok"));
        AddPatientStatusCommand addCommand = new AddPatientStatusCommand(INDEX_FIRST_PERSON, newStatus);

        Patient editedPatient = new PatientBuilder(CHARLIE).withStatus("Doing good", "Feeling ok").build();

        String expectedMessage =
                String.format(AddPatientStatusCommand.MESSAGE_ADD_STATUS_SUCCESS, newStatus, editedPatient);

        Model expectedModel = new ModelManager(getTypicalPatientist(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_THIRD_PERSON);
        Patientist patientist = (Patientist) expectedModel.getPatientist();
        patientist.removePerson(expectedModel.getFilteredPersonList().get(0));
        expectedModel.addPatient(editedPatient, expectedModel.getWard("Block A Ward 1"));
        expectedModel.getPatientist().updatePersonList();

        assertCommandSuccess(addCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AddPatientStatusCommand addPatientStatusCommand =
                new AddPatientStatusCommand(outOfBoundIndex, List.of(new PatientStatusDetails("Doing fine")));

        assertCommandFailure(addPatientStatusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of patientist book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of patientist book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getPatientist().getPersonList().size());

        AddPatientStatusCommand addPatientStatusCommand =
                new AddPatientStatusCommand(outOfBoundIndex, List.of(new PatientStatusDetails("Doing fine")));

        assertCommandFailure(addPatientStatusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        List<PatientStatusDetails> status = List.of(new PatientStatusDetails("Doing fine"));
        final AddPatientStatusCommand standardCommand =
                new AddPatientStatusCommand(INDEX_FIRST_PERSON, status);

        // same values -> returns true
        AddPatientStatusCommand commandWithSameValues =
                new AddPatientStatusCommand(INDEX_FIRST_PERSON, List.of(new PatientStatusDetails("Doing fine")));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new AddPatientStatusCommand(INDEX_SECOND_PERSON, status)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new AddPatientStatusCommand(INDEX_FIRST_PERSON,
                List.of(new PatientStatusDetails("Not doing fine")))));
    }
}
