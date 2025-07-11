package com.ecommerce.project.exceptions;

public class ResourseNotFoundException extends  RuntimeException{
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourseNotFoundException( String resourceName,String field, String fieldName) {
        super(String.format("%s not found with %s:%s ",resourceName,field,fieldName));
        this.field = field;
        this.fieldName = fieldName;
        this.resourceName = resourceName;
    }

    public ResourseNotFoundException( String resourceName,  String field,Long fieldId) {
        super(String.format("%s not found with %s: %d ",resourceName,field,fieldId));
        this.resourceName = resourceName;
        this.fieldId = fieldId;
        this.field = field;
    }
}
