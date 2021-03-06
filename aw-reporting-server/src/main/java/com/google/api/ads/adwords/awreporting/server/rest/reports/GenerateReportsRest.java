//Copyright 2012 Google Inc. All Rights Reserved.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package com.google.api.ads.adwords.awreporting.server.rest.reports;

import com.google.api.ads.adwords.awreporting.processors.ReportProcessor;
import com.google.api.ads.adwords.awreporting.server.RunnableReport;
import com.google.api.ads.adwords.awreporting.server.rest.AbstractBaseResource;
import com.google.api.ads.adwords.awreporting.server.rest.RestServer;

import org.restlet.representation.Representation;
import org.restlet.service.TaskService;

import java.util.Date;

/**
 * 
 * @author jtoledo
 * 
 */
public class GenerateReportsRest extends AbstractBaseResource {

  static TaskService taskService = new TaskService();

  public Representation getHandler() {
    String result = null;
    try {

      Long topAccountId = getParameterAsLong("topAccountId");
      Date dateStart = getParameterAsDate("dateStart");
      Date dateEnd = getParameterAsDate("dateEnd");

      // Generate Reports at MCC level for dates
      if (topAccountId != null && dateStart != null && dateEnd != null ) { 

        getContext().getParameters().add("maxThreads", "512");

        ReportProcessor processor = RestServer.getApplicationContext().getBean(ReportProcessor.class);

        // Launching a new Service(Thread) to make the request async.
        RunnableReport runnableReport = new RunnableReport(topAccountId, processor, RestServer.getProperties(), dateStart, dateEnd);

        taskService.submit(runnableReport);

        result = "OK - Task created, this usually takes 10-15mins for 1000 accounts/month";
      } else {
        taskService.getContext();
      }
    } catch (Exception exception) {
      return handleException(exception);
    }
    addReadOnlyHeaders();
    return createJsonResult(result);
  }
}
