/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.agiletec.aps.system.common.entity.model.attribute;

/**
 * This interface is for 'Text' Attributes that support multiple languages.
 *
 * @author E.Santoboni
 */
public interface ITextAttribute {

    /**
     * Return the 'text' contained in this attribute in the current or in the default language if the former is not available.
     *
     * @return The associated text.
     */
    String getText();

    /**
     * Return the 'text' contained in this attribute in the given language.
     *
     * @param langCode The code of the language.
     * @return The requested text.
     */
    String getTextForLang(String langCode);

    /**
     * Associate a text in a given language to this attribute.
     *
     * @param text The text to associate.
     * @param langCode The code of the language.
     */
    void setText(String text, String langCode);

    /**
     * Check whether the texts of this attribute needs a the conversion of the 'special characters' before the rendering process (in the
     * presentation layer).
     *
     * @return true if the text need special treatment for the HTML entities, false otherwise.
     */
    boolean needToConvertSpecialCharacter();

    /**
     * Return the max length of the text.
     *
     * @return The maximum length of the text.
     */
    int getMaxLength();

    /**
     * Set up the maximum length of the text.
     *
     * @param maxLength The maximum length of the text.
     * @deprecated to guaranted compatibility with previsous version of jAPS 2.0.12
     */
    void setMaxLength(int maxLength);

    /**
     * Get the minimum length of the text.
     *
     * @return The minimum length of the text.
     */
    int getMinLength();

    /**
     * Set up the minimum length of the text.
     *
     * @param minLength The minimum length of the text.
     * @deprecated to guaranted compatibility with previsous version of jAPS 2.0.12
     */
    void setMinLength(int minLength);

    /**
     * Get the regular expression.
     *
     * @return The regular expression.
     */
    String getRegexp();

    /**
     * Return the regular expression.
     *
     * @param regexp The regular expression.
     * @deprecated to guaranted compatibility with previsous version of jAPS 2.0.12
     */
    void setRegexp(String regexp);

}