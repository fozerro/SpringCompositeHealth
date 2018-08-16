package com.example.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.actuate.health.AbstractHealthAggregator;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.health.Service1;
import com.example.health.Service2;

/**
 * This is a simple controller which exposes 1 endpoint and also sub-classes
 * CompositeHealthIndicator. This allows us to aggregate several sub-components
 * into a single health status
 * 
 * @author Jim Marshall
 *
 */
@Controller
@Configurable
public class MyHelloWorldController extends CompositeHealthIndicator {

	/**
	 * This is a fairly basic aggregator which iterates through all the passed in
	 * status object and as long as one is up then it indicate the system itself is
	 * up
	 * 
	 * @author Jim Marshall
	 *
	 */
	private static class MyAggregator extends AbstractHealthAggregator {
		@Override
		protected Status aggregateStatus(List<Status> candidates) {
			Status ret = Status.DOWN;
			for (Status candidate : candidates) {
				if (candidate.equals(Status.UP)) {
					ret = Status.UP;
					// as long as one is up, we are up.
					break;
				}
			}
			return ret;
		}
	}

	private static Random rand = new Random(System.currentTimeMillis());

	// Holders for our sub-components. We save this only because we want to be able
	// to manually update whether they are up or down
	private Service1 serv1;
	private Service2 serv2;

	public MyHelloWorldController() {
		// Create an aggregator and pass it to the super class
		super(new MyAggregator());
		// Create the sub-component monitors
		this.serv1 = new Service1();
		addHealthIndicator("Service 1", serv1);

		this.serv2 = new Service2();
		addHealthIndicator("Service 2", serv2);
	}

	@GetMapping("/hello")
	@ResponseBody
	public String helloWorld() {
		// As part of the handler, randomly set the sub-components as up or down
		serv1.setAsUp(rand.nextBoolean());
		serv2.setAsUp(rand.nextBoolean());

		return "Hello Spring World!";
	}
}
