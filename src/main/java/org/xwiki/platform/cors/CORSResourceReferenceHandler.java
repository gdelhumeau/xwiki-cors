/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.platform.cors;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.container.Container;
import org.xwiki.container.Response;
import org.xwiki.container.servlet.ServletResponse;
import org.xwiki.resource.AbstractResourceReferenceHandler;
import org.xwiki.resource.ResourceReference;
import org.xwiki.resource.ResourceReferenceHandlerChain;
import org.xwiki.resource.ResourceReferenceHandlerException;
import org.xwiki.resource.ResourceType;
import org.xwiki.webjars.internal.WebJarsResourceReference;

/**
 * Allow cross domain request for some resources.
 * 
 * CORS means Cross-Origin Resource Sharing. 
 * 
 * Without the appropriate header in the response serving some resources (e.g: a font), the browser does not use them if
 * they come from an other domain. See <a href="http://jira.xwiki.org/browse/XWIKI-11300">XWIKI-11300</a> for an example
 * of use-case.
 * 
 * @since 7.1M2, 7.1.2
 * @version $Id: $
 */
@Component
@Named("cors")
@Singleton
public class CORSResourceReferenceHandler extends AbstractResourceReferenceHandler<ResourceType> implements
    Initializable
{
    @Inject
    private Container container;
            
    @Override
    public List<ResourceType> getSupportedResourceReferences()
    {
        return Arrays.asList(WebJarsResourceReference.TYPE);
    }

    @Override
    public void handle(ResourceReference reference, ResourceReferenceHandlerChain chain)
            throws ResourceReferenceHandlerException
    {
        Response response = this.container.getResponse();
        if (response instanceof ServletResponse) {
            ((ServletResponse) response).getHttpServletResponse().setHeader("Access-Control-Allow-Origin", "*");
        }

        // Be a good citizen, continue the chain, in case some lower-priority Handler has something to do for this
        // Resource Reference.
        chain.handleNext(reference);
    }

    @Override
    public void initialize() throws InitializationException
    {
        // TODO: remove this when http://jira.xwiki.org/browse/XWIKI-12250 is fixed
        // we actually don't care of the number...
        setPriority(1500);
    }
}
