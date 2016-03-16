package dash.processmanagement;

import dash.processmanagement.lead.Lead;
import dash.processmanagement.offer.Offer;
import dash.processmanagement.sale.Sale;
import dash.processmanagement.service.IProcessService;
import dash.processmanagement.status.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Andreas on 12.10.2015.
 */
@RestController
@RequestMapping("/api/rest/processes")
@Api(value = "Process API")
public class ProcessResource {

    @Autowired
    private ProcessRepository processRepository;
    
    @Autowired
    private IProcessService processService;

    @ApiOperation(value = "Returns all processes.", notes = "")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Process> get() { 
	return processRepository.findAll(); 
    }

    @ApiOperation(value = "Returns processes with a certain state", notes = "")
    @RequestMapping(method = RequestMethod.GET,
                    value= "state/{status}")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Process> get(@ApiParam(required=true) @PathVariable Status status) {
        return processRepository.findProcessesByStatus(status);
    }

    @ApiOperation(value = "Returns a single process.", notes = "")
    @RequestMapping(method = RequestMethod.GET,
                    	value="{id}")
    @ResponseStatus(HttpStatus.OK)
    public Process findById(@ApiParam(required=true) @PathVariable Long id) {
        return processRepository.findOne(id);
    }
    
    @ApiOperation(value = "Update a single process.", notes = "")
    @RequestMapping(method=RequestMethod.PUT,
                    value="{id}",
                    consumes = {MediaType.APPLICATION_JSON_VALUE},
                    produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Process update(@ApiParam(required=true) @PathVariable Long id, @ApiParam(required=true) @RequestBody Process updateProcess) {
        Process process = processRepository.findOne(id);

        process.setLead(updateProcess.getLead());
        process.setOffer(updateProcess.getOffer());
        process.setSale(updateProcess.getSale());
        process.setStatus(updateProcess.getStatus());

        processRepository.save(process);
        
        return process;
    }

    @ApiOperation(value = "Delete a single process.", notes = "")
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@ApiParam(required=true) @PathVariable Long id) {
        processRepository.delete(id);
    }
    
    @ApiOperation(value = "Returns a .", notes = "")
    @RequestMapping(value="/status/{status}/{kind}/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<?> getElementsByStatus(@ApiParam(required=true) @PathVariable Status status, String kind) {    
	return processService.getElementsByStatus(status, kind);
    }
    
    @ApiOperation(value = "Return a single lead.", notes = "")
    @RequestMapping(value = "/{processId}/leads", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Lead getLeadByProcess(@PathVariable Long processId) { 
	return processRepository.findOne(processId).getLead(); 
    }
    
    @ApiOperation(value = "Returns single offer.", notes = "")
    @RequestMapping(value="{processId}/offers", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Offer getOfferByProcess(@PathVariable Long processId) { 
	return processRepository.findOne(processId).getOffer(); 
    }
    
    @ApiOperation(value = "Returns a single sale.", notes = "")
    @RequestMapping(value="{processId}/sales", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Sale getSaleByProcess(@PathVariable Long processId) { 
	return processRepository.findOne(processId).getSale(); 
    }
}
