package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.status.*;

public class ScoreVisitor implements ValidationStatusVisitor {
    @Override
    public void visitCallMatchedMultipleSchemas(CallMatchedMultipleSchemas callMatchedMultipleSchemas) {

    }

    @Override
    public void visitCallNotMatchedBySchema(CallNotMatchedBySchema callNotMatchedBySchema) {

    }

    @Override
    public void visitResponseDoesNotMatchContentType(ResponseDoesNotMatchContentType responseDoesNotMatchContentType) {

    }

    @Override
    public void visitSchemaValidationFailed(SchemaValidationFailed schemaValidationFailed) {

    }

    @Override
    public void visitUntestedSchema(UntestedSchema untestedSchema) {

    }

    @Override
    public void visitValidationPassed(ValidationPassed validationPassed) {

    }
}
