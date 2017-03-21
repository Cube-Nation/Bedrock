package de.cubenation.api.bedrock.service;

import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;

public interface ServiceInterface {

    void init() throws ServiceInitException;

    void reload() throws ServiceReloadException;

}
