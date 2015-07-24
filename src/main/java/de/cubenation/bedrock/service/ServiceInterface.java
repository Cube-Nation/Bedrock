package de.cubenation.bedrock.service;

import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;

public interface ServiceInterface {

     void init() throws ServiceInitException;

    void reload() throws ServiceReloadException;

}
