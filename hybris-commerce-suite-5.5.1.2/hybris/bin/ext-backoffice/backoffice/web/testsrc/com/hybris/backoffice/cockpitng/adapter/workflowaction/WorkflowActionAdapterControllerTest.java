package com.hybris.backoffice.cockpitng.adapter.workflowaction;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;


@DeclaredInputs(@DeclaredInput(value = WorkflowActionAdapterController.SOCKET_IN_NODE_SELECTED, socketType = NavigationNode.class))
public class WorkflowActionAdapterControllerTest extends AbstractWidgetUnitTest<WorkflowActionAdapterController>
{

	@Mock
	protected WidgetInstanceManager widgetInstanceManager;

	@Mock
	protected DefaultWidgetModel widgetModel;

	@InjectMocks
	private final WorkflowActionAdapterController controller = Mockito.spy(new WorkflowActionAdapterController());

	@Override
	protected WorkflowActionAdapterController getWidgetController()
	{
		return controller;
	}
}
