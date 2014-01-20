package cn.jhc.um.vaadin.js;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.JavaScriptFunction;

/**
 * UMEditor component in server side, some code is copyed from {@link com.vaadin.ui.AbstractField}.
 * @author luyanfei
 *
 */
@JavaScript({"http://libs.baidu.com/jquery/1.10.2/jquery.min.js","umeditor.config.js", 
	"umeditor.js","umeditor-connector.js"})
@SuppressWarnings("serial")
public class UMEditorField extends AbstractJavaScriptComponent implements Field<String>{
	
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
    
	private boolean isListeningToPropertyEvents = false;
    
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
	
	public void setValue(String newValue) {
		getState().value = newValue;
//		System.err.println("setValue is invoked: " + newValue);
	}
	
	public String getValue() {
		return getState().value;
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
	}

	@Override
	public void discard() throws SourceException {
	}

	@Override
	public void setBuffered(boolean buffered) {
		
	}

	@Override
	public boolean isBuffered() {
		return buffered;
	}

	@Override
	public boolean isModified() {
		return false;
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
		return false;
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
	}

	@Override
	public void addListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
	}

	@Override
	public void removeValueChangeListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
	}

	@Override
	public void removeListener(
			com.vaadin.data.Property.ValueChangeListener listener) {
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		final String oldValue = getValue();
		removePropertyListeners();
		dataSource = newDataSource;
		if(dataSource != null) {
			setValue(dataSource.getValue());
		}
		addPropertyListeners();
        // Copy the validators from the data source
        if (dataSource instanceof Validatable) {
            final Collection<Validator> validators = ((Validatable) dataSource).getValidators();
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
        fireEvent(new AbstractField.ValueChangeEvent(this));
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
	
	   /**
     * Registers this as an event listener for events sent by the data source
     * (if any). Does nothing if
     * <code>isListeningToPropertyEvents == true</code>.
     */
    private void addPropertyListeners() {
        if (!isListeningToPropertyEvents ) {
            if (dataSource instanceof Property.ValueChangeNotifier) {
                ((Property.ValueChangeNotifier) dataSource).addValueChangeListener(this);
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
