package cn.jhc.um.vaadin.js;

import java.lang.reflect.Method;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.JavaScriptFunction;

/**
 * UMEditor component in server side, some code is copyed from
 * {@link com.vaadin.ui.AbstractField}.
 * 
 * @author luyanfei
 * 
 */
@JavaScript({ "http://libs.baidu.com/jquery/1.10.2/jquery.min.js",
		"umeditor.config.js", "umeditor.js", "umeditor-connector.js" })
@SuppressWarnings("serial")
public class UMEditorField extends AbstractJavaScriptComponent implements
		Field<String> {

	/**
	 * The error message for the exception that is thrown when the field is
	 * required but empty.
	 */
	private String requiredError = "";
	/**
	 * Connected data-source.
	 */
	private Property<String> dataSource = null;
	/**
	 * True if field is in buffered mode, false otherwise
	 */
	private boolean buffered;
	/**
	 * Current source exception.
	 */
	private Buffered.SourceException currentBufferedSourceException = null;

	private boolean isListeningToPropertyEvents = false;
	private boolean valueWasModifiedByDataSourceDuringCommit;
	/**
	 * Flag to indicate that the field is currently committing its value to the
	 * datasource.
	 */
	private boolean committingValueToDataSource = false;
	
    /* Value change events */

    private static final Method VALUE_CHANGE_METHOD;

    static {
        try {
            VALUE_CHANGE_METHOD = Property.ValueChangeListener.class
                    .getDeclaredMethod("valueChange",
                            new Class[] { Property.ValueChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractField");
        }
    }

	public UMEditorField() {
		addFunction("onValueChange", new JavaScriptFunction() {

			@Override
			public void call(JSONArray arguments) throws JSONException {
				setValue(arguments.getString(0));
			}
		});
	}

	@Override
	protected UMEditorState getState() {
		return (UMEditorState) super.getState();
	}

	@Override
	protected UMEditorState getState(boolean markAsDirty) {
		return (UMEditorState) super.getState(markAsDirty);
	}

	public void setValue(String newValue) {
		setValue(newValue, false);
	}

	protected void setValue(String newValue, boolean repaintIsNotNeeded) {

		getState().value = newValue;
		setModified(dataSource != null);

		valueWasModifiedByDataSourceDuringCommit = false;
		// In not buffering, try to commit
		if (!isBuffered() && dataSource != null) {
			try {

				// Commits the value to datasource
				committingValueToDataSource = true;
				dataSource.setValue(newValue);

				// The buffer is now unmodified
				setModified(false);

			} catch (final Throwable e) {

				// Sets the buffering state
				currentBufferedSourceException = new Buffered.SourceException(
						this, e);
				markAsDirty();

				// Throws the source exception
				throw currentBufferedSourceException;
			} finally {
				committingValueToDataSource = false;
			}
		}

		// If successful, remove set the buffering state to be ok
		if (getCurrentBufferedSourceException() != null) {
			setCurrentBufferedSourceException(null);
		}

		if (valueWasModifiedByDataSourceDuringCommit) {
			/*
			 * Value was modified by datasource. Force repaint even if repaint
			 * was not requested.
			 */
			valueWasModifiedByDataSourceDuringCommit = repaintIsNotNeeded = false;
		}
		// Fires the value change
		fireValueChange(repaintIsNotNeeded);
	}

	public String getValue() {
		// Give the value from abstract buffers if the field if possible
		if (dataSource == null || isBuffered() || isModified()) {
			return getState().value;
		}
		return dataSource.getValue();
	}

	@Override
	public void focus() {
		super.focus();
	}

	@Override
	public boolean isInvalidCommitted() {
		return false;
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted) {
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		if (dataSource != null) {
			try {

				// Commits the value to datasource.
				valueWasModifiedByDataSourceDuringCommit = false;
				committingValueToDataSource = true;
				dataSource.setValue(getValue());
			} catch (final Throwable e) {

				// Sets the buffering state.
				SourceException sourceException = new Buffered.SourceException(
						this, e);
				setCurrentBufferedSourceException(sourceException);

				// Throws the source exception.
				throw sourceException;
			} finally {
				committingValueToDataSource = false;
			}

		}

		// The abstract field is not modified anymore
		if (isModified()) {
			setModified(false);
		}

		// If successful, remove set the buffering state to be ok
		if (getCurrentBufferedSourceException() != null) {
			setCurrentBufferedSourceException(null);
		}

		if (valueWasModifiedByDataSourceDuringCommit) {
			valueWasModifiedByDataSourceDuringCommit = false;
			fireValueChange(false);
		}
	}

	@Override
	public void discard() throws SourceException {
		updateValueFromDataSource();
	}

	/**
	 * Copyed from AbstractField. Sets the buffered mode of this Field.
	 * <p>
	 * When the field is in buffered mode, changes will not be committed to the
	 * property data source until {@link #commit()} is called.
	 * </p>
	 * <p>
	 * Setting buffered mode from true to false will commit any pending changes.
	 * </p>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @since 7.0.0
	 * @param buffered
	 *            true if buffered mode should be turned on, false otherwise
	 */
	@Override
	public void setBuffered(boolean buffered) {
		if (this.buffered == buffered) {
			return;
		}
		this.buffered = buffered;
		if (!buffered) {
			commit();
		}
	}

	@Override
	public boolean isBuffered() {
		return buffered;
	}

	@Override
	public boolean isModified() {
		return getState(false).modified;
	}

	private void setModified(boolean modified) {
		getState().modified = modified;
	}

	@Override
	public void addValidator(Validator validator) {
	}

	@Override
	public void removeValidator(Validator validator) {
	}

	@Override
	public void removeAllValidators() {
	}

	@Override
	public Collection<Validator> getValidators() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void validate() throws InvalidValueException {
	}

	@Override
	public boolean isInvalidAllowed() {
		return false;
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed)
			throws UnsupportedOperationException {
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	public void addValueChangeListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
        addListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
	}

	@Override
	public void addListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
		addValueChangeListener(listener);
	}

	@Override
	public void removeValueChangeListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
        removeListener(AbstractField.ValueChangeEvent.class, listener,
                VALUE_CHANGE_METHOD);
	}

	@Override
	public void removeListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
		removeValueChangeListener(listener);
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		if (!isBuffered()) {
			if (committingValueToDataSource) {
				boolean propertyNotifiesOfTheBufferedValue = SharedUtil.equals(
						event.getProperty().getValue(), getValue());
				if (!propertyNotifiesOfTheBufferedValue) {
					/*
					 * Property (or chained property like PropertyFormatter) now
					 * reports different value than the one the field has just
					 * committed to it. In this case we respect the property
					 * value.
					 * 
					 * Still, we don't fire value change yet, but instead
					 * postpone it until "commit" is done. See setValue(Object,
					 * boolean) and commit().
					 */
					readValueFromProperty(event);
					valueWasModifiedByDataSourceDuringCommit = true;
				}
			} else if (!isModified()) {
				readValueFromProperty(event);
				fireValueChange(false);
			}
		}
	}

	private void readValueFromProperty(
			com.vaadin.data.Property.ValueChangeEvent event) {
		setValue((String) event.getProperty().getValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		final String oldValue = getValue();
		removePropertyListeners();
		dataSource = newDataSource;
		if (dataSource != null) {
			setValue(dataSource.getValue());
			setModified(false);
			if (getCurrentBufferedSourceException() != null) {
				setCurrentBufferedSourceException(null);
			}
		}
		addPropertyListeners();
		// Copy the validators from the data source
		if (dataSource instanceof Validatable) {
			final Collection<Validator> validators = ((Validatable) dataSource)
					.getValidators();
			if (validators != null) {
				for (Validator validator : validators) {
					addValidator(validator);
				}
			}
		}

		// Fires value change if the value has changed
		String value = getValue();
		if ((value != oldValue)
				&& ((value != null && !value.equals(oldValue)) || value == null)) {
			fireValueChange(false);
		}
	}

	@Override
	public Property getPropertyDataSource() {
		return dataSource;
	}

	/**
	 * Emits the value change event. The value contained in the field is
	 * validated before the event is created.
	 */
	protected void fireValueChange(boolean repaintIsNotNeeded) {
		fireEvent(new Field.ValueChangeEvent(this));
		if (!repaintIsNotNeeded) {
			markAsDirty();
		}
	}

	@Override
	public int getTabIndex() {
		return 0;
	}

	@Override
	public void setTabIndex(int tabIndex) {
	}

	@Override
	public boolean isRequired() {
		return getState().required;
	}

	@Override
	public void setRequired(boolean required) {
		getState().required = required;
	}

	@Override
	public void setRequiredError(String requiredMessage) {
		requiredError = requiredMessage;
		markAsDirty();
	}

	@Override
	public String getRequiredError() {
		return requiredError;
	}

	@Override
	public void attach() {
		super.attach();
		if (!isListeningToPropertyEvents) {
			addPropertyListeners();
			if (!isModified() && !isBuffered()) {
				// Update value from data source
				updateValueFromDataSource();
			}
		}
	}

	private void updateValueFromDataSource() {
		if (dataSource != null) {
			String newValue = dataSource.getValue();
			// If successful, remove set the buffering state to be ok
			if (getCurrentBufferedSourceException() != null) {
				setCurrentBufferedSourceException(null);
			}
			final boolean wasModified = isModified();
			setModified(false);

			// If the new value differs from the previous one
			if (!SharedUtil.equals(newValue, getValue())) {
				setValue(newValue);
				fireValueChange(false);
			} else if (wasModified) {
				// If the value did not change, but the modification status did
				markAsDirty();
			}
		}
	}

	public void setCurrentBufferedSourceException(
			Buffered.SourceException sourceException) {
		this.currentBufferedSourceException = sourceException;
		markAsDirty();
	}

	public Buffered.SourceException getCurrentBufferedSourceException() {
		return currentBufferedSourceException;
	}

	@Override
	public void detach() {
		super.detach();
		removePropertyListeners();
	}

	/**
	 * Registers this as an event listener for events sent by the data source
	 * (if any). Does nothing if
	 * <code>isListeningToPropertyEvents == true</code>.
	 */
	private void addPropertyListeners() {
		if (!isListeningToPropertyEvents) {
			if (dataSource instanceof Property.ValueChangeNotifier) {
				((Property.ValueChangeNotifier) dataSource)
						.addValueChangeListener(this);
			}
			isListeningToPropertyEvents = true;
		}
	}

	/**
	 * Stops listening to events sent by the data source (if any). Does nothing
	 * if <code>isListeningToPropertyEvents == false</code>.
	 */
	private void removePropertyListeners() {
		if (isListeningToPropertyEvents) {
			if (dataSource instanceof Property.ValueChangeNotifier) {
				((Property.ValueChangeNotifier) dataSource)
						.removeValueChangeListener(this);
			}
			isListeningToPropertyEvents = false;
		}
	}
}
