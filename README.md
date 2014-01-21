umeditor-vaadin-js
==================

Vaadin wrapper for umeditor(https://github.com/campaign/umeditor). umeditor is the mini version of ueditor(http://ueditor.baidu.com/website/), which is a versatile rich text editor. umeditor has been used in http://tieba.baidu.com, and has proven to be an efficient editor. umeditor-vaadin-js dependends on umeditor-fragment(https://github.com/luyanfei/umeditor-fragment)

## Example Code
```
	        final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		
		final PropertysetItem item = new PropertysetItem();
		final ObjectProperty<String> article = new ObjectProperty<String>("Initial content.");
		final ObjectProperty<String> title = new ObjectProperty<String>("This is title.");
		item.addItemProperty("title", title);
		item.addItemProperty("art", article);
		
		final UMEditorField field = new UMEditorField();
		layout.addComponent(field);
		
		final TextArea titleArea = new TextArea();
		layout.addComponent(titleArea);
		
		final FieldGroup binder = new FieldGroup(item);
		binder.bind(field, "art");
		binder.bind(titleArea, "title");

		Button button = new Button("Submit");
		button.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					binder.commit();
				} catch (CommitException e) {
					e.printStackTrace();
				}
				Notification.show("umeditorfield: " + field.getValue() 
						+"\numeditor property value: " + article.getValue()
						+"\ntitlefield: " + titleArea.getValue()
						+"\ntitle property value: " + title.getValue(),Notification.Type.WARNING_MESSAGE);
				
			}
		});
		layout.addComponent(button);
```
