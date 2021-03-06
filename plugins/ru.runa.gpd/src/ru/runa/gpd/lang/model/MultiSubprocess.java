package ru.runa.gpd.lang.model;

import java.util.List;

import org.eclipse.core.resources.IFile;

import ru.runa.gpd.extension.VariableFormatArtifact;
import ru.runa.gpd.extension.VariableFormatRegistry;
import ru.runa.gpd.lang.ValidationError;
import ru.runa.gpd.util.MultiinstanceParameters;
import ru.runa.gpd.util.VariableMapping;

public class MultiSubprocess extends Subprocess implements IMultiInstancesContainer {
    
    @Override
    public void validate(List<ValidationError> errors, IFile definitionFile) {
        super.validate(errors, definitionFile);
        MultiinstanceParameters parameters = new MultiinstanceParameters(variableMappings);
        if (!parameters.isValid(true)) {
            errors.add(ValidationError.createLocalizedError(this, "multiinstance.noMultiinstanceLink"));
        }
    }

    @Override
    protected boolean isCompatibleVariables(VariableMapping mapping, Variable variable1, Variable variable2) {
        if (mapping.isMultiinstanceLink() && VariableFormatRegistry.isApplicable(variable1, List.class.getName())) {
            if (variable1.getFormatComponentClassNames().length > 0) {
                String format = variable1.getFormatComponentClassNames()[0];
                VariableFormatArtifact elementArtifact = VariableFormatRegistry.getInstance().getArtifact(format);
                if (elementArtifact != null) {
                    return VariableFormatRegistry.isApplicable(variable2, elementArtifact.getJavaClassName());
                } else {
                    // user type
                    return VariableFormatRegistry.isApplicable(variable2, format);
                }
            }
            // back compatibility
            return true;
        }
        return super.isCompatibleVariables(mapping, variable1, variable2);
    }
}
