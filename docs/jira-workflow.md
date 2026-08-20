# Jira workflow mapping

Lastro does not replace Jira. Jira coordinates people and delivery dates; Lastro holds the engineering contract and evidence.

| Lastro status | Suggested Jira status | Exit evidence |
|---|---|---|
| DRAFT | Refinement | Problem and acceptance criteria written |
| IN_REVIEW | Technical review | Reviewer comment and risk analysis |
| APPROVED | Ready for development | Approved specification |
| IMPLEMENTING | In progress / code review | Tests, analysis, security and notes |
| RELEASED | Done | Deployment evidence and audit event |

Store the Lastro key in a Jira custom field and add the Jira issue URL to the specification discussion. Automation is intentionally postponed until the boundary is validated with a real team.
