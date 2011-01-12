/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.RunToLineHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.resources.IUniformResource;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class RunToLineAdapter implements IRunToLineTarget
{
	/**
	 * @see org.eclipse.debug.ui.actions.IRunToLineTarget#runToLine(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection, org.eclipse.debug.core.model.ISuspendResume)
	 */
	public void runToLine(IWorkbenchPart part, ISelection selection, ISuspendResume target) throws CoreException
	{
		IEditorPart editorPart = (IEditorPart) part;
		final IEditorInput input = editorPart.getEditorInput();
		String errorMessage = null;
		if (input == null)
		{
			errorMessage = Messages.RunToLineAdapter_EmptyEditor;
		}
		else
		{
			ITextEditor textEditor = (ITextEditor) editorPart;
			IDocument document = textEditor.getDocumentProvider().getDocument(input);
			if (document == null)
			{
				errorMessage = Messages.RunToLineAdapter_MissingDocument;
			}
			else
			{
				final Object[] resource = new Object[1];
				final int[] lineNumber = new int[] { -1 };
				final ITextSelection textSelection = (ITextSelection) selection;
				Runnable r = new Runnable()
				{
					public void run()
					{
						resource[0] = (IResource) input.getAdapter(IFile.class);
						if (resource[0] == null)
						{
							resource[0] = (IUniformResource) input.getAdapter(IUniformResource.class);
							if (resource[0] == null)
							{
								return;
							}
						}
						lineNumber[0] = textSelection.getStartLine() + 1;
						// XXX: fix wrong line number
					}
				};
				BusyIndicator.showWhile(DebugUiPlugin.getStandardDisplay(), r);
				if (lineNumber[0] > 0)
				{
					IBreakpoint breakpoint = null;
					Map<String, Object> attributes = new HashMap<String, Object>();
					attributes.put(IBreakpoint.PERSISTED, Boolean.FALSE);
					attributes.put(IJSDebugConstants.RUN_TO_LINE, Boolean.TRUE);
					breakpoint = JSDebugModel.createLineBreakpointForResource(resource[0], lineNumber[0], attributes,
							false);
					errorMessage = Messages.RunToLineAdapter_UnableToLocateDebugTarget;
					if (target instanceof IAdaptable)
					{
						IDebugTarget debugTarget = (IDebugTarget) ((IAdaptable) target).getAdapter(IDebugTarget.class);
						if (debugTarget != null)
						{
							RunToLineHandler handler = new RunToLineHandler(debugTarget, target, breakpoint);
							handler.run(new NullProgressMonitor());
							return;
						}
					}
				}
				else
				{
					// invalid line
					if (textSelection.getLength() > 0)
					{
						errorMessage = Messages.RunToLineAdapter_SelectedLineIsNotValidLocationToRunTo;
					}
					else
					{
						errorMessage = Messages.RunToLineAdapter_CursorPositionIsNotValidLocationToRunTo;
					}
				}
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, DebugUiPlugin.getUniqueIdentifier(), IStatus.OK,
				errorMessage, null));
	}

	/**
	 * @see org.eclipse.debug.ui.actions.IRunToLineTarget#canRunToLine(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection, org.eclipse.debug.core.model.ISuspendResume)
	 */
	public boolean canRunToLine(IWorkbenchPart part, ISelection selection, ISuspendResume target)
	{
		if (target instanceof IDebugElement)
		{
			IDebugElement element = (IDebugElement) target;
			IJSDebugTarget adapter = (IJSDebugTarget) element.getDebugTarget().getAdapter(IJSDebugTarget.class);
			return adapter != null;
		}
		return false;
	}
}
