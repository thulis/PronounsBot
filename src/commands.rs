use serenity::client::Context;
use serenity::framework::standard::macros::{command, group};
use serenity::framework::standard::CommandResult;
use serenity::model::channel::Message;

pub mod users {
    use super::*;

    #[command]
    pub async fn add(ctx: &Context, msg: &Message) -> CommandResult {
        // check whether guild id is available
        if let None = msg.guild_id {
            return Err("Guild id not available!");
        }

        let pronoun = match msg.iter().nth(1) {
            None => {
                msg.reply(ctx, "Unknown command!").await?;
                return Err("Unknown command!");
            }
            Some(prn) => prn,
        }
        .await;

        let current_roles = match msg.member {
            None => None,
            Some(m) => Some(m.roles),
        }
        .await;

        msg.guild_id
            .unwrap()
            .edit_member(ctx.http, msg.author, |member| {
                let roles: Vec<RoleID> = match current_roles {
                    None => Vec::with_capacity(1),
                    Some(m) => m,
                };
                roles.add(pronoun.unwrap());
                member.roles(&roles);
            })
            .await?;

        Ok(())
    }

    #[command]
    pub async fn remove(ctx: &Context, msg: &Message) -> CommandResult {
        let pronoun = match msg.iter().nth(1) {
            None => {
                msg.reply(ctx, "Unknown command!");
                return Err("Unknown command!");
            }
            Some(prn) => prn,
        }
        .await;

        let new_roles = match msg.member {
            None => None,
            Some(roles) => Some(roles.iter().filter(|e| !(e == pronoun))),
        }
        .await;

        msg.guild_id
            .unwrap()
            .edit_member(ctx.http, msg.author, |member| {
                let roles: Vec<RoleID> = match new_roles {
                    // no roles to be removed
                    None => Vec::new(),
                    Some(m) => m,
                };
                member.roles(&roles);
            })
            .await?;

        Ok(())
    }

    #[command]
    pub async fn list(_: &Context, _: &Message) -> CommandResult {
        Ok(())
    }

    #[group]
    #[commands(add, remove, list)]
    pub struct UserCommands;
}

pub mod admins {
    use super::*;

    #[command]
    pub async fn radd(_: &Context, msg: &Message) -> CommandResult {
        //msg.guild_id.unwrap().create_role()
        Ok(())
    }

    #[command]
    pub async fn rremove(_: &Context, _: &Message) -> CommandResult {
        //msg.guild_id.unwrap().delete_role()
        Ok(())
    }

    #[group]
    #[commands(radd, rremove)]
    pub struct AdminCommands;
}
