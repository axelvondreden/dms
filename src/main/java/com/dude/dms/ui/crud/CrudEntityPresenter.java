package com.dude.dms.ui.crud;

import com.dude.dms.app.security.CurrentUser;
import com.dude.dms.backend.data.entity.DataEntity;
import com.dude.dms.backend.service.CrudService;
import com.dude.dms.ui.utils.messages.CrudErrorMessage;
import com.dude.dms.ui.views.HasNotifications;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.function.Consumer;

public class CrudEntityPresenter<E extends DataEntity> {

    private final CrudService<E> crudService;

    private final CurrentUser currentUser;

    private final HasNotifications view;

    public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view) {
        this.crudService = crudService;
        this.currentUser = currentUser;
        this.view = view;
    }

    public void delete(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
        if (executeOperation(() -> crudService.delete(currentUser.getUser(), entity))) {
            onSuccess.accept(entity);
        } else {
            onFail.accept(entity);
        }
    }

    public void save(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
        if (executeOperation(() -> saveEntity(entity))) {
            onSuccess.accept(entity);
        } else {
            onFail.accept(entity);
        }
    }

    private boolean executeOperation(Runnable operation) {
        try {
            operation.run();
            return true;
        } catch (DataIntegrityViolationException e) {
            // Commit failed because of validation errors
            consumeError(CrudErrorMessage.OPERATION_PREVENTED_BY_REFERENCES, true);
        } catch (OptimisticLockingFailureException e) {
            consumeError(CrudErrorMessage.CONCURRENT_UPDATE, true);
        } catch (EntityNotFoundException e) {
            consumeError(CrudErrorMessage.ENTITY_NOT_FOUND, false);
        } catch (ConstraintViolationException e) {
            consumeError(CrudErrorMessage.REQUIRED_FIELDS_MISSING, false);
        } catch (RuntimeException e) {
            // Commit failed because of application-level data constraints
            consumeError(e.getMessage(), true);
        }
        return false;
    }

    private void consumeError(String message, boolean isPersistent) {
        view.showNotification(message, isPersistent);
    }

    private void saveEntity(E entity) {
        crudService.save(currentUser.getUser(), entity);
    }

    public boolean loadEntity(Long id, Consumer<E> onSuccess) {
        return executeOperation(() -> onSuccess.accept(crudService.load(id)));
    }
}
